import { useEffect, useState } from "react";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../Icon/Icon";

interface AdminRentalMediaImageProps {
  propertyId: number;
  mediaId?: number;
  alt: string;
  className?: string;
}

export function AdminRentalMediaImage({ propertyId, mediaId, alt, className }: AdminRentalMediaImageProps) {
  const api = useRentalApi();
  const [url, setUrl] = useState<string | null>(null);

  useEffect(() => {
    if (mediaId === undefined) return;
    let active = true;
    let objectUrl: string | null = null;
    api.getAdminPropertyMedia(propertyId, mediaId, "thumbnail")
      .then((blob) => {
        if (!active) return;
        objectUrl = URL.createObjectURL(blob);
        setUrl(objectUrl);
      })
      .catch(() => {
        if (active) setUrl(null);
      });
    return () => {
      active = false;
      if (objectUrl) URL.revokeObjectURL(objectUrl);
    };
  }, [api, mediaId, propertyId]);

  return url
    ? <img className={className} src={url} alt={alt} />
    : <span className={`admin-rental-media-placeholder${className ? ` ${className}` : ""}`}><Icon name="building" size={30} /></span>;
}
