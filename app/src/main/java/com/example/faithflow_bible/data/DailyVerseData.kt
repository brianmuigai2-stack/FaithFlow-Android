package com.example.faithflow_bible.data

data class VerseTheme(
    val name: String,
    val references: List<String>
)

data class CuratedVerse(
    val reference: String,
    val text: String,
    val translation: String = "WEB",
    val abbreviation: String = translation
)

object DailyVerseData {
    val themes: List<VerseTheme> = listOf(
        VerseTheme("Faith", listOf("John 3:16", "Romans 10:17", "Hebrews 11:1", "Hebrews 11:6", "2 Corinthians 5:7", "Galatians 2:20", "Ephesians 2:8", "Mark 11:24", "James 1:6", "1 Peter 1:8")),
        VerseTheme("Hope", listOf("Romans 15:13", "Jeremiah 29:11", "Psalm 42:11", "Isaiah 40:31", "Lamentations 3:24", "Hebrews 6:19", "Romans 5:5", "1 Peter 1:3", "Titus 2:13", "Psalm 71:5")),
        VerseTheme("Love", listOf("1 Corinthians 13:4", "1 Corinthians 13:13", "1 John 4:8", "1 John 4:19", "John 13:34", "Romans 5:8", "Colossians 3:14", "Ephesians 3:17", "Proverbs 10:12", "Song of Solomon 8:7")),
        VerseTheme("Strength", listOf("Philippians 4:13", "Isaiah 41:10", "Psalm 46:1", "Ephesians 6:10", "2 Corinthians 12:9", "Nehemiah 8:10", "Psalm 28:7", "Isaiah 40:29", "Joshua 1:9", "Habakkuk 3:19")),
        VerseTheme("Peace", listOf("John 14:27", "Philippians 4:7", "Isaiah 26:3", "Numbers 6:26", "Colossians 3:15", "Psalm 29:11", "Romans 5:1", "2 Thessalonians 3:16", "Matthew 5:9", "Psalm 4:8")),
        VerseTheme("Wisdom", listOf("James 1:5", "Proverbs 3:5", "Proverbs 2:6", "Proverbs 4:7", "Ecclesiastes 7:12", "Colossians 2:3", "Psalm 111:10", "Proverbs 16:16", "Daniel 2:20", "Job 28:28")),
        VerseTheme("Encouragement", listOf("Romans 8:31", "Deuteronomy 31:6", "Psalm 27:1", "2 Timothy 1:7", "John 16:33", "1 Thessalonians 5:11", "Hebrews 10:24", "Psalm 31:24", "Isaiah 43:2", "Romans 8:28")),
        VerseTheme("Healing", listOf("Jeremiah 17:14", "Psalm 147:3", "Isaiah 53:5", "James 5:15", "Psalm 103:3", "Proverbs 4:22", "Matthew 11:28", "3 John 1:2", "Exodus 15:26", "Mark 5:34")),
        VerseTheme("Prayer", listOf("1 Thessalonians 5:17", "Philippians 4:6", "Matthew 6:6", "Jeremiah 33:3", "James 5:16", "Romans 12:12", "Colossians 4:2", "1 John 5:14", "Psalm 145:18", "Mark 11:25")),
        VerseTheme("Praise", listOf("Psalm 150:6", "Psalm 100:4", "Psalm 34:1", "Hebrews 13:15", "Psalm 103:1", "Psalm 95:1", "Psalm 96:4", "Isaiah 25:1", "Psalm 63:3", "Revelation 5:13")),
        VerseTheme("God's Word", listOf("Psalm 119:105", "Hebrews 4:12", "2 Timothy 3:16", "Psalm 119:11", "Matthew 4:4", "Isaiah 40:8", "John 1:1", "Psalm 19:7", "Colossians 3:16", "Joshua 1:8")),
        VerseTheme("Salvation", listOf("Ephesians 2:8", "Romans 10:9", "Acts 4:12", "John 14:6", "Titus 3:5", "Romans 6:23", "Acts 16:31", "1 John 5:11", "2 Timothy 1:9", "John 10:9")),
        VerseTheme("Comfort", listOf("2 Corinthians 1:3", "Psalm 23:4", "Matthew 5:4", "Isaiah 49:13", "Psalm 34:18", "John 14:1", "Revelation 21:4", "Psalm 94:19", "Nahum 1:7", "Isaiah 66:13")),
        VerseTheme("Trust", listOf("Proverbs 3:5", "Psalm 56:3", "Psalm 37:5", "Jeremiah 17:7", "Psalm 9:10", "Isaiah 12:2", "Psalm 62:8", "Proverbs 16:3", "Psalm 118:8", "John 14:1")),
        VerseTheme("Courage", listOf("Joshua 1:9", "Deuteronomy 31:8", "Psalm 27:14", "1 Corinthians 16:13", "Isaiah 41:13", "2 Samuel 10:12", "Psalm 31:24", "Daniel 10:19", "Acts 4:29", "Proverbs 28:1")),
        VerseTheme("Joy", listOf("Nehemiah 8:10", "Psalm 16:11", "Philippians 4:4", "John 15:11", "Romans 15:13", "Psalm 30:5", "James 1:2", "1 Peter 1:8", "Psalm 118:24", "Galatians 5:22")),
        VerseTheme("Forgiveness", listOf("1 John 1:9", "Ephesians 4:32", "Colossians 3:13", "Psalm 103:12", "Isaiah 1:18", "Matthew 6:14", "Luke 6:37", "Acts 3:19", "Micah 7:18", "Daniel 9:9")),
        VerseTheme("Guidance", listOf("Psalm 32:8", "Proverbs 3:6", "Isaiah 30:21", "Psalm 25:4", "Psalm 48:14", "John 16:13", "James 1:5", "Psalm 73:24", "Proverbs 16:9", "Exodus 13:21")),
        VerseTheme("Light", listOf("John 8:12", "Matthew 5:14", "Psalm 27:1", "1 John 1:5", "Ephesians 5:8", "Psalm 119:130", "Isaiah 60:1", "2 Corinthians 4:6", "John 1:5", "Proverbs 4:18")),
        VerseTheme("Truth", listOf("John 14:6", "John 8:32", "Psalm 25:5", "John 17:17", "Ephesians 4:15", "3 John 1:4", "Psalm 86:11", "Proverbs 12:22", "Zechariah 8:16", "1 John 3:18")),
        VerseTheme("Mercy", listOf("Lamentations 3:22", "Micah 6:8", "Hebrews 4:16", "Psalm 86:5", "Luke 6:36", "Titus 3:5", "Ephesians 2:4", "Psalm 145:9", "James 2:13", "Matthew 5:7")),
        VerseTheme("Rest", listOf("Matthew 11:28", "Psalm 23:2", "Exodus 33:14", "Hebrews 4:9", "Psalm 127:2", "Mark 6:31", "Isaiah 40:31", "Psalm 62:1", "Jeremiah 31:25", "Psalm 4:8")),
        VerseTheme("Eternal Life", listOf("John 17:3", "Romans 6:23", "John 11:25", "1 John 5:13", "John 5:24", "Titus 1:2", "John 6:40", "1 Timothy 6:12", "Daniel 12:2", "Revelation 22:5")),
        VerseTheme("Worship", listOf("John 4:24", "Psalm 95:6", "Romans 12:1", "Psalm 29:2", "Revelation 4:11", "Matthew 4:10", "Psalm 99:5", "Hebrews 12:28", "Philippians 3:3", "Psalm 96:9")),
        VerseTheme("Grace", listOf("2 Corinthians 12:9", "Ephesians 2:8", "John 1:16", "Romans 3:24", "Titus 2:11", "Hebrews 4:16", "James 4:6", "1 Peter 5:10", "2 Timothy 2:1", "Romans 5:20")),
        VerseTheme("Assurance", listOf("Romans 8:38", "John 10:28", "Philippians 1:6", "1 John 5:13", "Hebrews 10:23", "Psalm 121:7", "Jude 1:24", "2 Thessalonians 3:3", "Isaiah 32:17", "2 Timothy 1:12")),
        VerseTheme("Repentance", listOf("Acts 3:19", "2 Chronicles 7:14", "Luke 13:3", "Proverbs 28:13", "Joel 2:13", "2 Peter 3:9", "Ezekiel 18:32", "Matthew 4:17", "Acts 17:30", "Psalm 51:10")),
        VerseTheme("God's Sovereignty", listOf("Psalm 115:3", "Daniel 4:35", "Romans 8:28", "Proverbs 19:21", "Isaiah 46:10", "Ephesians 1:11", "Job 42:2", "Psalm 103:19", "Colossians 1:17", "Revelation 19:6")),
        VerseTheme("Children & Blessing", listOf("Psalm 127:3", "Mark 10:14", "Proverbs 22:6", "Matthew 19:14", "3 John 1:4", "Numbers 6:24", "Psalm 128:3", "Isaiah 54:13", "Ephesians 6:1", "Luke 18:16"))
    )

    val references: List<String> = themes
        .flatMap { it.references }
        .distinct()

    val themeByReference: Map<String, String> = buildMap {
        themes.forEach { theme ->
            theme.references.forEach { reference ->
                putIfAbsent(reference, theme.name)
            }
        }
    }

    val curatedFallback: Map<String, CuratedVerse> = references.associateWith { reference ->
        CuratedVerse(
            reference = reference,
            text = "This verse is included in FaithFlow's bundled offline Bible. Open the passage to read ${reference}.",
        )
    } + mapOf(
        "John 3:16" to CuratedVerse("John 3:16", "For God so loved the world, that he gave his one and only Son, that whoever believes in him should not perish, but have eternal life."),
        "Philippians 4:13" to CuratedVerse("Philippians 4:13", "I can do all things through Christ, who strengthens me."),
        "Psalm 119:105" to CuratedVerse("Psalm 119:105", "Your word is a lamp to my feet, and a light for my path."),
        "Psalm 23:4" to CuratedVerse("Psalm 23:4", "Even though I walk through the valley of the shadow of death, I will fear no evil, for you are with me."),
        "Proverbs 3:5" to CuratedVerse("Proverbs 3:5", "Trust in Yahweh with all your heart, and don't lean on your own understanding."),
        "Romans 8:28" to CuratedVerse("Romans 8:28", "We know that all things work together for good for those who love God, to those who are called according to his purpose."),
        "Matthew 11:28" to CuratedVerse("Matthew 11:28", "Come to me, all you who labor and are heavily burdened, and I will give you rest."),
        "Isaiah 41:10" to CuratedVerse("Isaiah 41:10", "Don't you be afraid, for I am with you. Don't be dismayed, for I am your God."),
        "John 14:27" to CuratedVerse("John 14:27", "Peace I leave with you. My peace I give to you; not as the world gives, I give to you."),
        "Romans 15:13" to CuratedVerse("Romans 15:13", "Now may the God of hope fill you with all joy and peace in believing.")
    )

    val emergencyVerse = CuratedVerse(
        reference = "Psalm 119:105",
        text = "Your word is a lamp to my feet, and a light for my path."
    )
}
