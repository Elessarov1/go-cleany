import { useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import type { RentalPropertyMedia } from "../../domain/rental";
import { Icon } from "../Icon/Icon";

interface RentalGalleryProps {
  media: RentalPropertyMedia[];
  propertyTitle: string;
}

export function RentalGallery({ media, propertyTitle }: RentalGalleryProps) {
  const { t } = useTranslation();
  const viewportRef = useRef<HTMLDivElement>(null);
  const [activeIndex, setActiveIndex] = useState(0);
  const orderedMedia = useMemo(
    () => [...media].sort((first, second) => {
      if (first.cover !== second.cover) return first.cover ? -1 : 1;
      return first.sortOrder - second.sortOrder || first.id - second.id;
    }),
    [media],
  );

  useEffect(() => {
    setActiveIndex(0);
    viewportRef.current?.scrollTo({ left: 0 });
  }, [orderedMedia]);

  const show = (index: number) => {
    const nextIndex = Math.max(0, Math.min(index, orderedMedia.length - 1));
    const viewport = viewportRef.current;
    const target = viewport?.children.item(nextIndex) as HTMLElement | null;
    target?.scrollIntoView({ behavior: "smooth", block: "nearest", inline: "center" });
    setActiveIndex(nextIndex);
  };

  const updateActiveImage = () => {
    const viewport = viewportRef.current;
    if (!viewport || viewport.clientWidth === 0) return;
    const children = Array.from(viewport.children) as HTMLElement[];
    const viewportCenter = viewport.scrollLeft + viewport.clientWidth / 2;
    let nearestIndex = 0;
    let nearestDistance = Number.POSITIVE_INFINITY;
    children.forEach((child, index) => {
      const childCenter = child.offsetLeft + child.offsetWidth / 2;
      const distance = Math.abs(childCenter - viewportCenter);
      if (distance < nearestDistance) {
        nearestDistance = distance;
        nearestIndex = index;
      }
    });
    setActiveIndex(nearestIndex);
  };

  if (orderedMedia.length === 0) return null;

  return (
    <section className="rental-gallery" aria-label={t("rental.property.galleryLabel")}>
      <div className="rental-gallery__viewport" ref={viewportRef} onScroll={updateActiveImage}>
        {orderedMedia.map((item, index) => (
          <figure key={item.id} aria-label={t("rental.property.photoPosition", { current: index + 1, total: orderedMedia.length })}>
            <img
              src={item.url}
              srcSet={item.cardUrl ? `${item.cardUrl} 960w, ${item.url} 1600w` : undefined}
              sizes="(max-width: 720px) 100vw, 960px"
              alt={t("rental.property.photoAlt", { title: propertyTitle, index: index + 1 })}
              loading={index === 0 ? "eager" : "lazy"}
            />
            {item.cover ? <span>{t("rental.property.cover")}</span> : null}
          </figure>
        ))}
      </div>

      {orderedMedia.length > 1 ? (
        <>
          <button
            className="rental-gallery__arrow rental-gallery__arrow--previous"
            type="button"
            aria-label={t("rental.property.previousPhoto")}
            disabled={activeIndex === 0}
            onClick={() => show(activeIndex - 1)}
          >
            <Icon name="arrow-left" size={20} />
          </button>
          <button
            className="rental-gallery__arrow rental-gallery__arrow--next"
            type="button"
            aria-label={t("rental.property.nextPhoto")}
            disabled={activeIndex === orderedMedia.length - 1}
            onClick={() => show(activeIndex + 1)}
          >
            <Icon name="arrow-right" size={20} />
          </button>
          <div className="rental-gallery__dots" aria-label={t("rental.property.photoNavigation")}>
            {orderedMedia.map((item, index) => (
              <button
                className={index === activeIndex ? "is-active" : ""}
                key={item.id}
                type="button"
                aria-label={t("rental.property.openPhoto", { index: index + 1 })}
                aria-current={index === activeIndex ? "true" : undefined}
                onClick={() => show(index)}
              />
            ))}
          </div>
          <div className="rental-gallery__thumbnails" aria-label={t("rental.property.photoNavigation")}>
            {orderedMedia.map((item, index) => (
              <button
                className={index === activeIndex ? "is-active" : ""}
                key={item.id}
                type="button"
                aria-label={t("rental.property.openPhoto", { index: index + 1 })}
                aria-current={index === activeIndex ? "true" : undefined}
                onClick={() => show(index)}
              >
                <img src={item.thumbnailUrl ?? item.url} alt="" loading="lazy" />
              </button>
            ))}
          </div>
        </>
      ) : null}
    </section>
  );
}
