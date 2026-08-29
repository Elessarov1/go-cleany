export type ServiceCatalogSceneState = "idle" | "rent" | "cleaning";

interface ServiceCatalogSceneProps {
  activeService: ServiceCatalogSceneState;
}

export function ServiceCatalogScene({ activeService }: ServiceCatalogSceneProps) {
  return (
    <div
      className="service-catalog-scene"
      data-scene={activeService}
      aria-hidden="true"
    >
      <span className="service-catalog-scene__glow" />
      <svg
        className="service-catalog-scene__svg"
        viewBox="0 0 320 240"
        role="presentation"
        focusable="false"
      >
        <g className="scene-orbit scene-orbit--back">
          <ellipse cx="160" cy="122" rx="126" ry="78" />
        </g>

        <g className="scene-cloud scene-cloud--left">
          <circle cx="55" cy="56" r="13" />
          <circle cx="70" cy="51" r="17" />
          <circle cx="88" cy="58" r="12" />
          <rect x="48" y="57" width="48" height="15" rx="8" />
        </g>
        <g className="scene-cloud scene-cloud--right">
          <circle cx="236" cy="45" r="10" />
          <circle cx="249" cy="40" r="14" />
          <circle cx="264" cy="47" r="10" />
          <rect x="229" y="47" width="43" height="12" rx="7" />
        </g>

        <g className="scene-house">
          <path className="scene-house__roof" d="M86 105 160 57l74 48v12H86z" />
          <rect className="scene-house__body" x="94" y="105" width="132" height="92" rx="23" />
          <rect className="scene-house__window" x="116" y="126" width="27" height="30" rx="8" />
          <rect className="scene-house__window" x="177" y="126" width="27" height="30" rx="8" />
          <rect className="scene-house__door" x="146" y="151" width="29" height="46" rx="10" />
          <circle className="scene-house__handle" cx="168" cy="174" r="3" />
        </g>

        <g className="scene-plant">
          <path d="M75 196c5-19 14-28 27-32-1 15-9 26-27 32Z" />
          <path d="M77 196c-2-15-9-24-20-29 0 14 6 23 20 29Z" />
          <path className="scene-plant__stem" d="M77 196v-29" />
        </g>

        <g className="scene-service scene-service--cleaning">
          <g className="scene-bubble scene-bubble--one"><circle cx="88" cy="93" r="9" /></g>
          <g className="scene-bubble scene-bubble--two"><circle cx="63" cy="117" r="5" /></g>
          <path className="scene-spark scene-spark--one" d="M248 91c0 11-5 16-16 16 11 0 16 5 16 16 0-11 5-16 16-16-11 0-16-5-16-16Z" />
          <path className="scene-spark scene-spark--two" d="M224 71c0 6-3 9-9 9 6 0 9 3 9 9 0-6 3-9 9-9-6 0-9-3-9-9Z" />
          <path className="scene-cleaning-sweep" d="M111 185c28-7 67-8 99-1" />
        </g>

        <g className="scene-service scene-service--rent">
          <g className="scene-key">
            <circle cx="246" cy="111" r="13" />
            <path d="M236 121 214 143h-11v11h-11v-12l34-34" />
          </g>
          <path className="scene-door-light" d="M151 157h19v34h-19z" />
          <path className="scene-rent-line" d="M224 177c15 0 26-8 32-23" />
        </g>

        <g className="scene-orbit scene-orbit--front">
          <circle className="scene-orbit__dot scene-orbit__dot--one" cx="46" cy="142" r="5" />
          <circle className="scene-orbit__dot scene-orbit__dot--two" cx="274" cy="145" r="4" />
        </g>
      </svg>
    </div>
  );
}
