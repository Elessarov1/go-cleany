package com.cleany.admin;

import java.util.Arrays;

public record AdminIssuePhotoContent(String contentType, byte[] content) {

    public AdminIssuePhotoContent {
        content = Arrays.copyOf(content, content.length);
    }

    @Override
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }
}
