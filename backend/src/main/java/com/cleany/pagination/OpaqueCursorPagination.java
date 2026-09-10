package com.cleany.pagination;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public final class OpaqueCursorPagination {
    public static final int DEFAULT_SIZE = 20;
    public static final int MAXIMUM_SIZE = 50;

    private OpaqueCursorPagination() {
    }

    public static <S, T> CursorPageResponse<T> descending(
            List<S> source, String rawCursor, Integer requestedSize,
            Function<S, Instant> createdAt, ToLongFunction<S> id, Function<S, T> mapper
    ) {
        int size = requestedSize == null ? DEFAULT_SIZE : requestedSize;
        if (size < 1 || size > MAXIMUM_SIZE) throw new InvalidCursorException();
        CursorKey cursor = decode(rawCursor);
        List<S> eligible = source.stream()
                .filter(item -> cursor == null || isAfterCursor(item, cursor, createdAt, id))
                .limit(size + 1L)
                .toList();
        boolean hasMore = eligible.size() > size;
        List<S> page = hasMore ? new ArrayList<>(eligible.subList(0, size)) : eligible;
        String nextCursor = hasMore && !page.isEmpty()
                ? encode(createdAt.apply(page.getLast()), id.applyAsLong(page.getLast()))
                : null;
        return new CursorPageResponse<>(page.stream().map(mapper).toList(), nextCursor, hasMore);
    }

    private static <S> boolean isAfterCursor(S item, CursorKey cursor, Function<S, Instant> createdAt,
                                             ToLongFunction<S> id) {
        Instant itemTime = createdAt.apply(item);
        int comparison = itemTime.compareTo(cursor.createdAt());
        return comparison < 0 || comparison == 0 && id.applyAsLong(item) < cursor.id();
    }

    public static String encode(Instant instant, long id) {
        String value = instant.getEpochSecond() + ":" + instant.getNano() + ":" + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static CursorKey decode(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(raw), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", -1);
            if (parts.length != 3) throw new InvalidCursorException();
            return new CursorKey(Instant.ofEpochSecond(Long.parseLong(parts[0]), Long.parseLong(parts[1])),
                    Long.parseLong(parts[2]));
        } catch (IllegalArgumentException exception) {
            throw new InvalidCursorException();
        }
    }

    public record CursorKey(Instant createdAt, long id) {
    }
}
