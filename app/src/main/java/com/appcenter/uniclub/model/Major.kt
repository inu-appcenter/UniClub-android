package com.appcenter.uniclub.model

enum class College(val displayName: String) {
    //학부
    HUMANITIES("인문대학"),
    NATURAL_SCIENCES("자연과학대학"),
    SOCIAL_SCIENCES("사회과학대학"),
    GLOBAL_ECONOMICS("글로벌정경대학"),
    ENGINEERING("공과대학"),
    INFORMATION_TECH("정보기술대학"),
    BUSINESS("경영대학"),
    ARTS_SPORTS("예술체육대학"),
    EDUCATION("사범대학"),
    URBAN_SCIENCE("도시과학대학"),
    BIO_TECH("생명과학기술대학"),
    FREE_MAJOR("융합자유전공대학"),
    INTERNATIONAL_TRADE("동북아국제통상학부"),
    LAW("법학부"),

    //대학원
    GRAD_HUMANITIES("인문사회계열"),
    GRAD_SCIENCE("자연과학계열"),
    GRAD_ENGINEERING("공학계열"),
    GRAD_ARTS_SPORTS("예술체육계열"),
    GRAD_LOGISTICS("동북아물류대학원"),
    GRAD_EDUCATION("교육대학원"),
    GRAD_POLICY("정책대학원"),
    GRAD_TECH_ENGINEERING("공학대학원"),
    GRAD_CULTURE("문화대학원")
}

enum class Major(val displayName: String, val college: College) {

    // 인문대학
    KOREAN_LANGUAGE_LITERATURE("국어국문학과", College.HUMANITIES),
    ENGLISH_LANGUAGE_LITERATURE("영어영문학과", College.HUMANITIES),
    GERMAN_LANGUAGE_LITERATURE("독어독문학과", College.HUMANITIES),
    FRENCH_LANGUAGE_LITERATURE("불어불문학과", College.HUMANITIES),
    JAPANESE_REGIONAL_CULTURE("일본지역문화학과", College.HUMANITIES),
    CHINESE_LANGUAGE_LITERATURE("중어중국학과", College.HUMANITIES),

    // 자연과학대학
    MATHEMATICS("수학과", College.NATURAL_SCIENCES),
    PHYSICS("물리학과", College.NATURAL_SCIENCES),
    CHEMISTRY("화학과", College.NATURAL_SCIENCES),
    FASHION_INDUSTRY("패션산업학과", College.NATURAL_SCIENCES),
    MARINE_SCIENCE("해양학과", College.NATURAL_SCIENCES),

    // 사회과학대학
    SOCIAL_WELFARE("사회복지학과", College.SOCIAL_SCIENCES),
    MEDIA_COMMUNICATION("미디어커뮤니케이션학과", College.SOCIAL_SCIENCES),
    LIBRARY_INFORMATION_SCIENCE("문헌정보학과", College.SOCIAL_SCIENCES),
    CREATIVE_TALENT_DEVELOPMENT("창의인재개발학과", College.SOCIAL_SCIENCES),

    // 글로벌정경대학
    PUBLIC_ADMINISTRATION("행정학과", College.GLOBAL_ECONOMICS),
    POLITICAL_SCIENCE_DIPLOMACY("정치외교학과", College.GLOBAL_ECONOMICS),
    ECONOMICS("경제학과", College.GLOBAL_ECONOMICS),
    TRADE("무역학부", College.GLOBAL_ECONOMICS),
    CONSUMER_STUDIES("소비자학과", College.GLOBAL_ECONOMICS),

    // 공과대학
    MECHANICAL_ENGINEERING("기계공학과", College.ENGINEERING),
    ELECTRICAL_ENGINEERING("전기공학과", College.ENGINEERING),
    ELECTRONIC_ENGINEERING("전자공학부", College.ENGINEERING),
    INDUSTRIAL_MANAGEMENT_ENGINEERING("산업경영공학과", College.ENGINEERING),
    MATERIALS_SCIENCE_ENGINEERING("신소재공학과", College.ENGINEERING),
    SAFETY_ENGINEERING("안전공학과", College.ENGINEERING),
    ENERGY_CHEMICAL_ENGINEERING("에너지화학공학과", College.ENGINEERING),
    BIO_ROBOT_SYSTEM_ENGINEERING("바이오-로봇시스템공학과", College.ENGINEERING),

    // 정보기술대학
    COMPUTER_ENGINEERING("컴퓨터공학부", College.INFORMATION_TECH),
    INFORMATION_COMMUNICATION_ENGINEERING("정보통신공학과", College.INFORMATION_TECH),
    EMBEDDED_SYSTEM_ENGINEERING("임베디드시스템공학과", College.INFORMATION_TECH),

    // 경영대학
    BUSINESS_ADMINISTRATION("경영학부", College.BUSINESS),
    DATA_SCIENCE("데이터과학과", College.BUSINESS),
    TAXATION_ACCOUNTING("세무회계학과", College.BUSINESS),
    TECHNO_MANAGEMENT("테크노경영학과", College.BUSINESS),

    // 예술체육대학
    KOREAN_PAINTING("한국화전공(조형예술학부)", College.ARTS_SPORTS),
    WESTERN_PAINTING("서양화전공(조형예술학부)", College.ARTS_SPORTS),
    DESIGN("디자인학부", College.ARTS_SPORTS),
    PERFORMING_ARTS("공연예술학과", College.ARTS_SPORTS),
    SPORTS_SCIENCE("스포츠과학부", College.ARTS_SPORTS),
    EXERCISE_HEALTH("운동건강학부", College.ARTS_SPORTS),

    // 사범대학
    KOREAN_EDUCATION("국어교육과", College.EDUCATION),
    ENGLISH_EDUCATION("영어교육과", College.EDUCATION),
    JAPANESE_EDUCATION("일어교육과", College.EDUCATION),
    MATHEMATICS_EDUCATION("수학교육과", College.EDUCATION),
    PHYSICAL_EDUCATION("체육교육과", College.EDUCATION),
    EARLY_CHILDHOOD_EDUCATION("유아교육과", College.EDUCATION),
    HISTORY_EDUCATION("역사교육과", College.EDUCATION),
    ETHICS_EDUCATION("윤리교육과", College.EDUCATION),

    // 도시과학대학
    URBAN_ADMINISTRATION("도시행정학과", College.URBAN_SCIENCE),
    CONSTRUCTION_ENVIRONMENTAL_ENGINEERING("도시환경공학부(건설환경공학전공)", College.URBAN_SCIENCE),
    ENVIRONMENTAL_ENGINEERING("도시환경공학부(환경공학전공)", College.URBAN_SCIENCE),
    URBAN_ENGINEERING("도시공학과", College.URBAN_SCIENCE),
    ARCHITECTURAL_ENGINEERING("도시건축학부(건축공학전공)", College.URBAN_SCIENCE),
    URBAN_ARCHITECTURE("도시건축학부(도시건축학전공)", College.URBAN_SCIENCE),

    // 생명과학기술대학
    LIFE_SCIENCE("생명과학부(생명과학전공)", College.BIO_TECH),
    MOLECULAR_LIFE_SCIENCE("생명과학부(분자의생명전공)", College.BIO_TECH),
    BIOTECHNOLOGY("생명공학부(생명공학전공)", College.BIO_TECH),
    NANO_BIOTECHNOLOGY("생명공학부(나노바이오공학전공)", College.BIO_TECH),

    // 융합자유전공대학
    FREE_MAJOR("자유전공학부", College.FREE_MAJOR),
    INTERNATIONAL_FREE_MAJOR("국제자유전공학부", College.FREE_MAJOR),
    CONVERGENCE("융합학부", College.FREE_MAJOR),

    // 동북아국제통상학부
    NORTHEAST_ASIA_INTERNATIONAL_COMMERCE("동북아국제통상전공", College.INTERNATIONAL_TRADE),
    SMART_LOGISTICS_ENGINEERING("스마트물류공학전공", College.INTERNATIONAL_TRADE),
    IBE("IBE전공", College.INTERNATIONAL_TRADE),

    // 법학부 (독립학부)
    LAW("법학부", College.LAW),


    //일반 대학원

    // 인문사회계열
    KOREAN_LANGUAGE_EDUCATION("한국어교육학과", College.GRAD_HUMANITIES),
    CHINESE_STUDIES("중국학과", College.GRAD_HUMANITIES),
    EDUCATION("교육학과", College.GRAD_HUMANITIES),
    ETHICS("윤리학과", College.GRAD_HUMANITIES),
    LAW_DEPARTMENT("법학과", College.GRAD_HUMANITIES),
    BUSINESS_ADMINISTRATION_DEPARTMENT("경영학과", College.GRAD_HUMANITIES),
    TRADE_DEPARTMENT("무역학과", College.GRAD_HUMANITIES),
    NORTHEAST_ASIA_COMMERCE("동북아통상학과", College.GRAD_HUMANITIES),
    URBAN_PLANNING_POLICY("도시계획·정책학과(협동과정)", College.GRAD_HUMANITIES),
    EARLY_CHILDHOOD_FOREST_NATURE_EDUCATION("유아·숲·자연교육학과(협동과정)", College.GRAD_HUMANITIES),
    TOURISM_CONVENTION_ENTERTAINMENT("관광컨벤션엔터테인먼트학과", College.GRAD_HUMANITIES),

    // 자연과학계열
    LIFE_SCIENCE_DEPARTMENT("생명과학과", College.GRAD_SCIENCE),
    CLOTHING_TEXTILES("의류학과", College.GRAD_SCIENCE),
    BEAUTY_INDUSTRY("뷰티산업학과", College.GRAD_SCIENCE),

    // 공학계열
    COMPUTER_ENGINEERING_DEPARTMENT("컴퓨터공학과", College.GRAD_ENGINEERING),
    CONSTRUCTION_ENVIRONMENTAL_ENGINEERING_DEPARTMENT("건설환경공학과", College.GRAD_ENGINEERING),
    ENVIRONMENTAL_ENERGY_ENGINEERING("환경에너지공학과", College.GRAD_ENGINEERING),
    URBAN_CONSTRUCTION_ENGINEERING("도시건설공학과", College.GRAD_ENGINEERING),
    ARCHITECTURE("건축학과", College.GRAD_ENGINEERING),
    LIFE_NANO_BIOTECHNOLOGY("생명·나노바이오공학과", College.GRAD_ENGINEERING),
    CLIMATE_INTERNATIONAL_COOPERATION("기후국제협력학과(협동과정)", College.GRAD_ENGINEERING),
    URBAN_CONVERGENCE_COMPLEX("도시융ㆍ복합학과(협동과정)", College.GRAD_ENGINEERING),
    INTELLIGENT_SEMICONDUCTOR_ENGINEERING("지능형반도체공학과(협동과정)", College.GRAD_ENGINEERING),
    ARTIFICIAL_INTELLIGENCE("인공지능학과(협동과정)", College.GRAD_ENGINEERING),
    FUTURE_MOBILITY("미래모빌리티학과(협동과정)", College.GRAD_ENGINEERING),
    BIO_HEALTH_CONVERGENCE("바이오헬스융합학과(협동과정)", College.GRAD_ENGINEERING),

    // 예술체육계열
    PHYSICAL_EDUCATION_DEPARTMENT("체육학과", College.GRAD_ARTS_SPORTS),
    FINE_ARTS("미술학과", College.GRAD_ARTS_SPORTS),
    DESIGN_DEPARTMENT("디자인학과", College.GRAD_ARTS_SPORTS),


    //동북아 물류 대학원
    LOGISTICS_MANAGEMENT("물류경영학과", College.GRAD_LOGISTICS),
    LOGISTICS_SYSTEM("융합물류시스템학과", College.GRAD_LOGISTICS),

    //교육 대학원
    EDUCATIONAL_ADMINISTRATION_LEADERSHIP("교육행정·리더십전공", College.GRAD_EDUCATION),
    INSTRUCTIONAL_DESIGN_CONSULTING("수업설계·수업컨설팅 전공", College.GRAD_EDUCATION),
    LIFELONG_VOCATIONAL_EDUCATION("평생·직업교육전공", College.GRAD_EDUCATION),
    COUNSELING_PSYCHOLOGY("상담심리전공", College.GRAD_EDUCATION),
    CREATIVITY_GIFTED_EDUCATION("창의성·영재교육전공", College.GRAD_EDUCATION),
    CHILD_ART_PSYCHOTHERAPY("아동 예술심리치료전공", College.GRAD_EDUCATION),
    MEDIA_EDUCATION("미디어교육전공", College.GRAD_EDUCATION),
    MECHANICAL_EDUCATION("기계교육전공", College.GRAD_EDUCATION),
    ART_EDUCATION("미술교육전공", College.GRAD_EDUCATION),
    SPORTS_CULTURE_ADMINISTRATION("스포츠문화행정전공", College.GRAD_EDUCATION),

    //정책 대학원
    JUDICIAL_ADMINISTRATION("사법행정학과", College.GRAD_POLICY),
    CRISIS_MANAGEMENT("위기관리학과", College.GRAD_POLICY),
    LEGISLATIVE_SECURITY_STUDIES("의회정치·안보정책학과", College.GRAD_POLICY),

    //공학 대학원
    URBAN_ENGINEERING_MAJOR("도시공학전공", College.GRAD_TECH_ENGINEERING),
    SAFETY_ENVIRONMENTAL_SYSTEM_ENGINEERING("안전환경시스템공학전공", College.GRAD_TECH_ENGINEERING),
    CONVERGENCE_DESIGN("융합디자인전공", College.GRAD_TECH_ENGINEERING),
    ARCHITECTURAL_DESIGN_ENGINEERING("건축학전공", College.GRAD_TECH_ENGINEERING),

    //문화 대학원
    LOCAL_CULTURE("지역문화학과", College.GRAD_CULTURE)
}