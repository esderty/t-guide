import type { Excursion } from '@/entities/excursion/model/types'

const coverImageUrl = '/illustrations/volga-route.svg'
const stopImageUrl = '/illustrations/landmark-card.svg'

type StopInput = Omit<Excursion['stops'][number], 'imageUrl' | 'audio'> & {
  audioDurationSeconds: number
  transcriptPreview: string
}

type ExcursionInput = Omit<Excursion, 'coverImageUrl' | 'stops'> & {
  stops: StopInput[]
}

function createStop({
  audioDurationSeconds,
  transcriptPreview,
  ...stop
}: StopInput): Excursion['stops'][number] {
  return {
    ...stop,
    imageUrl: stopImageUrl,
    audio: {
      id: `audio-${stop.id}`,
      url: null,
      durationSeconds: audioDurationSeconds,
      language: 'ru',
      transcriptPreview,
    },
  }
}

function createExcursion({
  stops,
  ...excursion
}: ExcursionInput): Excursion {
  return {
    ...excursion,
    coverImageUrl,
    stops: stops.map(createStop),
  }
}

export const excursionsMock: Excursion[] = [
  createExcursion({
    id: 1,
    slug: 'historical-center',
    createdAt: '2026-03-22',
    title: 'Исторический центр Самары',
    tagline: 'Площади, театры и главные городские символы',
    description:
      'Прогулка по центральной Самаре с выходом к набережной, архитектурными доминантами и узнаваемыми городскими пространствами.',
    theme: 'history',
    district: 'Ленинский район',
    durationMinutes: 110,
    distanceKm: 4.2,
    startLabel: 'Площадь Куйбышева',
    finishLabel: 'Самарская набережная',
    routeColor: '#D36B3F',
    difficulty: 'easy',
    stops: [
      {
        id: 'kuibyshev-square',
        order: 1,
        title: 'Площадь Куйбышева',
        shortDescription:
          'Одна из крупнейших площадей Европы и важная стартовая точка маршрута.',
        description:
          'Площадь Куйбышева помогает почувствовать масштаб исторического центра и открыть экскурсию с одной из самых узнаваемых городских сцен.',
        coordinates: { lat: 53.19517, lng: 50.10174 },
        expectedVisitMinutes: 12,
        audioDurationSeconds: 84,
        transcriptPreview:
          'История площади и ее роли в городских событиях.',
      },
      {
        id: 'opera-ballet',
        order: 2,
        title: 'Самарский театр оперы и балета',
        shortDescription:
          'Монументальное здание, которое формирует образ площади.',
        description:
          'Театр оперы и балета задает архитектурный тон всему маршруту и помогает увидеть Самару как большой культурный центр.',
        coordinates: { lat: 53.19443, lng: 50.10017 },
        expectedVisitMinutes: 15,
        audioDurationSeconds: 98,
        transcriptPreview:
          'Короткий рассказ о театре, площади и культурной жизни Самары.',
      },
      {
        id: 'drama-theatre',
        order: 3,
        title: 'Самарский театр драмы',
        shortDescription:
          'Красный силуэт театра давно стал частью городского образа.',
        description:
          'Театр драмы соединяет историю, городской рельеф и красивый видовой акцент в самом центре Самары.',
        coordinates: { lat: 53.19649, lng: 50.09856 },
        expectedVisitMinutes: 14,
        audioDurationSeconds: 90,
        transcriptPreview:
          'О театре, его фасаде и месте в визуальном образе города.',
      },
      {
        id: 'strukovsky-garden',
        order: 4,
        title: 'Струковский сад',
        shortDescription:
          'Зеленая пауза внутри маршрута по историческому центру.',
        description:
          'Струковский сад дает маршруту спокойный ритм, открывает тенистые аллеи и помогает мягко перейти от центра к Волге.',
        coordinates: { lat: 53.20028, lng: 50.09731 },
        expectedVisitMinutes: 18,
        audioDurationSeconds: 102,
        transcriptPreview:
          'Неспешная история старейшего городского сада и прогулочной культуры Самары.',
      },
      {
        id: 'volga-embankment',
        order: 5,
        title: 'Самарская набережная',
        shortDescription:
          'Финальная точка с большим видом на Волгу.',
        description:
          'Набережная завершает экскурсию сильным городским пейзажем и ощущением простора, ради которого в Самару хочется возвращаться.',
        coordinates: { lat: 53.20982, lng: 50.10915 },
        expectedVisitMinutes: 20,
        audioDurationSeconds: 95,
        transcriptPreview:
          'О Волге, набережной и том, почему именно здесь так хорошо завершать прогулку.',
      },
    ],
  }),
  createExcursion({
    id: 2,
    slug: 'modern-and-volga',
    createdAt: '2026-03-21',
    title: 'Самара: модерн и Волга',
    tagline: 'Особняки, городские детали и вид к воде',
    description:
      'Маршрут о самарском модерне, старых улицах и атмосферных местах, где архитектура встречается с городской прогулкой.',
    theme: 'modernism',
    district: 'Самарский район',
    durationMinutes: 95,
    distanceKm: 3.6,
    startLabel: 'Музей модерна',
    finishLabel: 'Жигулевский пивоваренный завод',
    routeColor: '#1F6F8B',
    difficulty: 'easy',
    stops: [
      {
        id: 'modern-museum',
        order: 1,
        title: 'Музей модерна',
        shortDescription:
          'Выразительная точка для знакомства с архитектурой рубежа веков.',
        description:
          'Музей модерна помогает сразу увидеть изящество фасадов и понять, почему Самара так интересна любителям архитектуры.',
        coordinates: { lat: 53.20452, lng: 50.11106 },
        expectedVisitMinutes: 16,
        audioDurationSeconds: 89,
        transcriptPreview:
          'Короткая история о самарском модерне и особняке Курлиной.',
      },
      {
        id: 'frunze-street',
        order: 2,
        title: 'Улица Фрунзе',
        shortDescription:
          'Историческая улица с выразительными фасадами и старой городской тканью.',
        description:
          'На Фрунзе важно не только смотреть на здания, но и чувствовать саму улицу: ее ритм, детали и плотность исторической застройки.',
        coordinates: { lat: 53.20261, lng: 50.10782 },
        expectedVisitMinutes: 20,
        audioDurationSeconds: 76,
        transcriptPreview:
          'О прогулке по старой улице и о том, как читать город по деталям.',
      },
      {
        id: 'iberian-monastery',
        order: 3,
        title: 'Иверский монастырь',
        shortDescription:
          'Тихая точка маршрута с вертикалями и панорамой.',
        description:
          'Иверский монастырь меняет настроение прогулки и добавляет в нее тишину, масштаб и спокойный городской обзор.',
        coordinates: { lat: 53.19895, lng: 50.10473 },
        expectedVisitMinutes: 17,
        audioDurationSeconds: 91,
        transcriptPreview:
          'История монастыря и панорамных видов в центре Самары.',
      },
      {
        id: 'zhiguli-brewery',
        order: 4,
        title: 'Жигулевский пивоваренный завод',
        shortDescription:
          'Легендарная индустриальная точка рядом с Волгой.',
        description:
          'Пивоваренный завод завершает маршрут узнаваемым городским образом и добавляет в экскурсию индустриальную историю Самары.',
        coordinates: { lat: 53.20303, lng: 50.09138 },
        expectedVisitMinutes: 18,
        audioDurationSeconds: 87,
        transcriptPreview:
          'О городской легенде, бренде Самары и индустриальном наследии.',
      },
    ],
  }),
  createExcursion({
    id: 3,
    slug: 'embankment-sunsets',
    createdAt: '2026-03-23',
    title: 'Набережные и закаты Самары',
    tagline: 'Прогулка вдоль Волги с лучшими видовыми точками',
    description:
      'Маршрут вдоль набережной и прилегающих пространств, который знакомит с городской жизнью у воды.',
    theme: 'waterfront',
    district: 'Октябрьский район',
    durationMinutes: 80,
    distanceKm: 3.1,
    startLabel: 'Ладья',
    finishLabel: 'Первомайский спуск',
    routeColor: '#2A8F77',
    difficulty: 'easy',
    stops: [
      {
        id: 'ladya',
        order: 1,
        title: 'Ладья',
        shortDescription:
          'Один из главных символов самарской набережной.',
        description:
          'Ладья открывает маршрут сильным визуальным акцентом и сразу задает прогулке волжское настроение.',
        coordinates: { lat: 53.22358, lng: 50.15562 },
        expectedVisitMinutes: 12,
        audioDurationSeconds: 78,
        transcriptPreview:
          'О символе набережной и современной городской прогулке у Волги.',
      },
      {
        id: 'beach-line',
        order: 2,
        title: 'Пляжная линия',
        shortDescription:
          'Пространство, где особенно заметен масштаб волжского берега.',
        description:
          'Здесь лучше всего чувствуется открытость Самары к воде и летний городской ритм набережной.',
        coordinates: { lat: 53.22134, lng: 50.14928 },
        expectedVisitMinutes: 16,
        audioDurationSeconds: 82,
        transcriptPreview:
          'Как набережная стала важной частью повседневной жизни города.',
      },
      {
        id: 'csk-pool',
        order: 3,
        title: 'Бассейн ЦСК ВВС',
        shortDescription:
          'Знаковая точка на прогулке вдоль воды.',
        description:
          'Район у бассейна помогает увидеть набережную не только как место отдыха, но и как часть городской истории.',
        coordinates: { lat: 53.21661, lng: 50.13759 },
        expectedVisitMinutes: 14,
        audioDurationSeconds: 74,
        transcriptPreview:
          'О набережной как городской сцене спорта, прогулок и встреч.',
      },
      {
        id: 'pervomaisky-descent',
        order: 4,
        title: 'Первомайский спуск',
        shortDescription:
          'Финальный участок с удобным выходом в центр.',
        description:
          'Первомайский спуск завершает маршрут мягким переходом от воды обратно к городской ткани центра.',
        coordinates: { lat: 53.21228, lng: 50.12684 },
        expectedVisitMinutes: 15,
        audioDurationSeconds: 79,
        transcriptPreview:
          'О связи набережной с городом и удобстве волжских маршрутов.',
      },
    ],
  }),
  createExcursion({
    id: 4,
    slug: 'merchant-samara',
    createdAt: '2026-03-20',
    title: 'Купеческая Самара',
    tagline: 'Особняки, доходные дома и старые городские улицы',
    description:
      'Маршрут по старой Самаре с купеческими историями, выразительными фасадами и домами, которые задавали характер городу.',
    theme: 'architecture',
    district: 'Самарский район',
    durationMinutes: 125,
    distanceKm: 4.8,
    startLabel: 'Улица Ленинградская',
    finishLabel: 'Хлебная площадь',
    routeColor: '#8A5A44',
    difficulty: 'medium',
    stops: [
      {
        id: 'leningradskaya-street',
        order: 1,
        title: 'Улица Ленинградская',
        shortDescription:
          'Пешеходная улица с плотным историческим окружением.',
        description:
          'Ленинградская помогает почувствовать купеческий масштаб Самары и увидеть, как историческая улица работает сегодня.',
        coordinates: { lat: 53.18843, lng: 50.09364 },
        expectedVisitMinutes: 18,
        audioDurationSeconds: 86,
        transcriptPreview:
          'О купеческой торговле, улице и живом центре старого города.',
      },
      {
        id: 'public-assembly-house',
        order: 2,
        title: 'Дом общественного собрания',
        shortDescription:
          'Одна из точек, через которые раскрывается городской быт прошлых лет.',
        description:
          'Через подобные здания особенно хорошо видно, как развивалась общественная жизнь старой Самары.',
        coordinates: { lat: 53.18996, lng: 50.09541 },
        expectedVisitMinutes: 15,
        audioDurationSeconds: 80,
        transcriptPreview:
          'О городской жизни и роли общественных пространств в Самаре.',
      },
      {
        id: 'merchant-mansion',
        order: 3,
        title: 'Купеческий особняк',
        shortDescription:
          'Фасады и детали, которые делают Самару узнаваемой.',
        description:
          'Особняки рассказывают о вкусе, статусе и визуальной культуре самарского купечества лучше любых длинных лекций.',
        coordinates: { lat: 53.18678, lng: 50.09973 },
        expectedVisitMinutes: 16,
        audioDurationSeconds: 84,
        transcriptPreview:
          'О фасадах, декоративных деталях и купеческом богатстве города.',
      },
      {
        id: 'old-yard',
        order: 4,
        title: 'Старый самарский двор',
        shortDescription:
          'Точка, где особенно чувствуется масштаб повседневной городской истории.',
        description:
          'Самарские дворы помогают увидеть город изнутри и понять, как жили люди за красивыми фасадами центральных улиц.',
        coordinates: { lat: 53.18488, lng: 50.10112 },
        expectedVisitMinutes: 14,
        audioDurationSeconds: 75,
        transcriptPreview:
          'Небольшой рассказ о дворах, быте и городской памяти.',
      },
      {
        id: 'bread-square',
        order: 5,
        title: 'Хлебная площадь',
        shortDescription:
          'Историческая торговая точка и логичный финал маршрута.',
        description:
          'Хлебная площадь помогает завершить экскурсию темой торговли и городской экономики старой Самары.',
        coordinates: { lat: 53.18245, lng: 50.10684 },
        expectedVisitMinutes: 20,
        audioDurationSeconds: 92,
        transcriptPreview:
          'О торговом прошлом Самары и роли площади в жизни города.',
      },
    ],
  }),
  createExcursion({
    id: 5,
    slug: 'samara-culture-route',
    createdAt: '2026-03-19',
    title: 'Культурная Самара',
    tagline: 'Театры, музеи и важные городские пространства',
    description:
      'Спокойный маршрут по культурным точкам города для тех, кто хочет пройти Самару через ее сцены, выставки и общественные места.',
    theme: 'culture',
    district: 'Ленинский район',
    durationMinutes: 90,
    distanceKm: 3.4,
    startLabel: 'Художественный музей',
    finishLabel: 'Филармония',
    routeColor: '#6D4C9C',
    difficulty: 'easy',
    stops: [
      {
        id: 'art-museum',
        order: 1,
        title: 'Самарский художественный музей',
        shortDescription:
          'Одна из главных музейных точек города.',
        description:
          'Художественный музей открывает маршрут знакомством с культурной жизнью Самары и ее выставочными пространствами.',
        coordinates: { lat: 53.18793, lng: 50.09192 },
        expectedVisitMinutes: 18,
        audioDurationSeconds: 88,
        transcriptPreview:
          'О музее, его коллекции и месте в культурной карте города.',
      },
      {
        id: 'dramatic-square',
        order: 2,
        title: 'Театральная площадь',
        shortDescription:
          'Точка, где встречаются архитектура и городская сцена.',
        description:
          'Пространство рядом с театрами помогает почувствовать, как культура работает не только в зданиях, но и в самом городе.',
        coordinates: { lat: 53.19601, lng: 50.09961 },
        expectedVisitMinutes: 14,
        audioDurationSeconds: 76,
        transcriptPreview:
          'О театральной жизни и городском культурном ритме.',
      },
      {
        id: 'gorky-library',
        order: 3,
        title: 'Библиотека имени Горького',
        shortDescription:
          'Тихая и важная культурная точка маршрута.',
        description:
          'Библиотека дает маршруту спокойную интонацию и показывает интеллектуальную сторону городской жизни.',
        coordinates: { lat: 53.19854, lng: 50.10731 },
        expectedVisitMinutes: 16,
        audioDurationSeconds: 82,
        transcriptPreview:
          'О чтении, городском знании и культурной памяти Самары.',
      },
      {
        id: 'samara-philharmonic',
        order: 4,
        title: 'Самарская филармония',
        shortDescription:
          'Финальная точка маршрута с музыкальным настроением.',
        description:
          'Филармония завершает маршрут темой городской музыки и культурного вечера в Самаре.',
        coordinates: { lat: 53.19172, lng: 50.10358 },
        expectedVisitMinutes: 17,
        audioDurationSeconds: 85,
        transcriptPreview:
          'О музыкальной жизни города и роли филармонии в Самаре.',
      },
    ],
  }),
  createExcursion({
    id: 6,
    slug: 'panorama-points',
    createdAt: '2026-03-18',
    title: 'Панорамы и смотровые точки',
    tagline: 'Лучшие виды на Волгу и городской рельеф',
    description:
      'Маршрут для тех, кто хочет увидеть Самару с видовых точек и прочувствовать ее масштаб через панорамы.',
    theme: 'panoramas',
    district: 'Октябрьский район',
    durationMinutes: 135,
    distanceKm: 5.7,
    startLabel: 'Софийский собор',
    finishLabel: 'Вертолетная площадка',
    routeColor: '#3E6BA5',
    difficulty: 'medium',
    stops: [
      {
        id: 'sofiysky-view',
        order: 1,
        title: 'Площадка у Софийского собора',
        shortDescription:
          'Стартовая точка с обзором городской ткани.',
        description:
          'Здесь удобно начать разговор о рельефе Самары, направлении к Волге и визуальной структуре города.',
        coordinates: { lat: 53.22685, lng: 50.15651 },
        expectedVisitMinutes: 16,
        audioDurationSeconds: 82,
        transcriptPreview:
          'О рельефе Самары и первых обзорных впечатлениях от маршрута.',
      },
      {
        id: 'volga-slope',
        order: 2,
        title: 'Волжский склон',
        shortDescription:
          'Точка, где хорошо читается связь города и реки.',
        description:
          'Склон помогает буквально увидеть, как Самара спускается к Волге и почему город так тесно связан с берегом.',
        coordinates: { lat: 53.23216, lng: 50.15041 },
        expectedVisitMinutes: 17,
        audioDurationSeconds: 84,
        transcriptPreview:
          'О волжском склоне, панорамах и ощущении открытого пространства.',
      },
      {
        id: 'barboshina-polyana',
        order: 3,
        title: 'Барбошина поляна',
        shortDescription:
          'Широкое пространство с другим ритмом городской прогулки.',
        description:
          'Барбошина поляна показывает Самару не только исторической, но и более просторной, современной и открытой.',
        coordinates: { lat: 53.24845, lng: 50.18946 },
        expectedVisitMinutes: 18,
        audioDurationSeconds: 86,
        transcriptPreview:
          'О перемене городского масштаба и прогулке к видовым точкам.',
      },
      {
        id: 'helicopter-platform',
        order: 4,
        title: 'Вертолетная площадка',
        shortDescription:
          'Финальный обзорный акцент маршрута.',
        description:
          'Вертолетная площадка завершает маршрут одним из самых сильных панорамных впечатлений от Самары и Волги.',
        coordinates: { lat: 53.28812, lng: 50.21463 },
        expectedVisitMinutes: 22,
        audioDurationSeconds: 94,
        transcriptPreview:
          'О видах на город, Волгу и любимых самарских смотровых точках.',
      },
    ],
  }),
  createExcursion({
    id: 7,
    slug: 'legends-of-old-samara',
    createdAt: '2026-03-17',
    title: 'Легенды старой Самары',
    tagline: 'Городские истории, мифы и необычные адреса',
    description:
      'Маршрут с атмосферой старого города, локальными легендами и местами, где особенно интересно слушать короткие истории.',
    theme: 'legends',
    district: 'Самарский район',
    durationMinutes: 105,
    distanceKm: 4.1,
    startLabel: 'Особняк Субботина-Шихобалова',
    finishLabel: 'Католический костел',
    routeColor: '#8B3E5E',
    difficulty: 'medium',
    stops: [
      {
        id: 'subbotin-mansion',
        order: 1,
        title: 'Особняк Субботина-Шихобалова',
        shortDescription:
          'Одна из самых атмосферных точек старой Самары.',
        description:
          'Такие дома особенно подходят для легенд, потому что сами фасады будто подсказывают городу истории.',
        coordinates: { lat: 53.19382, lng: 50.09782 },
        expectedVisitMinutes: 15,
        audioDurationSeconds: 83,
        transcriptPreview:
          'Небольшая история о знаменитом особняке и его городской ауре.',
      },
      {
        id: 'old-passage',
        order: 2,
        title: 'Старый пассаж',
        shortDescription:
          'Точка, где можно говорить о торговле и городских байках.',
        description:
          'Пассажи и старые торговые пространства всегда собирают вокруг себя легенды, слухи и характерные городские сюжеты.',
        coordinates: { lat: 53.19141, lng: 50.09506 },
        expectedVisitMinutes: 14,
        audioDurationSeconds: 78,
        transcriptPreview:
          'О старой торговле, слухах и самарских городских историях.',
      },
      {
        id: 'narrow-lane',
        order: 3,
        title: 'Старый переулок',
        shortDescription:
          'Маршрут через тихую часть старого города.',
        description:
          'Переулки особенно хорошо раскрывают атмосферу старой Самары и помогают сделать экскурсию камерной.',
        coordinates: { lat: 53.19007, lng: 50.10088 },
        expectedVisitMinutes: 13,
        audioDurationSeconds: 73,
        transcriptPreview:
          'О том, как маленькие улицы делают прогулку по-настоящему живой.',
      },
      {
        id: 'catholic-church',
        order: 4,
        title: 'Католический костел',
        shortDescription:
          'Выразительный финал маршрута с сильным силуэтом.',
        description:
          'Костел завершает маршрут красивым архитектурным образом и добавляет экскурсии ощущение городского разнообразия.',
        coordinates: { lat: 53.19344, lng: 50.10349 },
        expectedVisitMinutes: 18,
        audioDurationSeconds: 88,
        transcriptPreview:
          'О костеле, его архитектуре и легендах старой Самары.',
      },
    ],
  }),
  createExcursion({
    id: 8,
    slug: 'family-weekend',
    createdAt: '2026-03-16',
    title: 'Семейные выходные в Самаре',
    tagline: 'Спокойная прогулка по удобным и понятным точкам',
    description:
      'Небольшой маршрут для прогулки без спешки: понятные локации, удобная длина и комфортный темп для семейного отдыха.',
    theme: 'family',
    district: 'Октябрьский район',
    durationMinutes: 70,
    distanceKm: 2.8,
    startLabel: 'Загородный парк',
    finishLabel: 'Софийская набережная',
    routeColor: '#5B8E3C',
    difficulty: 'easy',
    stops: [
      {
        id: 'zagorodny-park',
        order: 1,
        title: 'Загородный парк',
        shortDescription:
          'Просторная зеленая зона для спокойного старта прогулки.',
        description:
          'Парк делает маршрут легким и дружелюбным, поэтому хорошо подходит для прогулки с детьми и отдыха без спешки.',
        coordinates: { lat: 53.24091, lng: 50.17412 },
        expectedVisitMinutes: 18,
        audioDurationSeconds: 77,
        transcriptPreview:
          'О семейных прогулках, отдыхе и зеленых пространствах Самары.',
      },
      {
        id: 'volga-view-path',
        order: 2,
        title: 'Волжская аллея',
        shortDescription:
          'Неспешный участок маршрута с приятным видом и удобной дорогой.',
        description:
          'Эта часть маршрута хороша своей простотой: идти легко, рядом красивые виды, а сама прогулка получается спокойной.',
        coordinates: { lat: 53.23674, lng: 50.16947 },
        expectedVisitMinutes: 15,
        audioDurationSeconds: 72,
        transcriptPreview:
          'Небольшая история о прогулках у Волги и городском отдыхе.',
      },
      {
        id: 'family-embankment-stop',
        order: 3,
        title: 'Софийская набережная',
        shortDescription:
          'Финальная точка маршрута у воды.',
        description:
          'Набережная завершает маршрут легким финалом и дает возможность остаться у Волги еще дольше, если захочется.',
        coordinates: { lat: 53.23168, lng: 50.16094 },
        expectedVisitMinutes: 16,
        audioDurationSeconds: 74,
        transcriptPreview:
          'О набережной как о месте отдыха, прогулок и семейного времени.',
      },
    ],
  }),
]
