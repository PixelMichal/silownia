package com.example.silownia.data

import java.util.Calendar
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class StepGoalPreset(
    val min: Int,
    val max: Int,
    val target: Int,
    val label: String
)

object JournalData {

    val BASE_EXERCISES = listOf(
        "Wyciskanie leżąc",
        "Przysiad ze sztangą",
        "Martwy ciąg",
        "Wyciskanie nad głowę",
        "Podciąganie",
        "Wiosłowanie sztangą",
        "Dipy",
        "Uginanie ramion",
        "Wyciskanie na skosie",
        "Wypychanie na suwnicy",
        "Martwy ciąg rumuński",
        "Wspięcia na palce"
    )

    val PRESET_SUPPS = listOf(
        "Kreatyna" to "3–5 g",
        "Witamina D3" to "wg opakowania",
        "Omega-3" to "wg opakowania",
        "Magnez" to "wg opakowania",
        "Odżywka białkowa" to "1 miarka"
    )

    val MEAL_TYPES = listOf(
        "sn" to "Śniadanie",
        "ds" to "II śniadanie",
        "ob" to "Obiad",
        "ko" to "Kolacja",
        "pr" to "Przekąska",
        "" to "Inne"
    )

    val STEP_GOALS = mapOf(
        "red" to StepGoalPreset(8000, 12000, 10000, "Redukcja"),
        "utr" to StepGoalPreset(7000, 10000, 8000, "Utrzymanie"),
        "masa" to StepGoalPreset(5000, 8000, 6000, "Zwiększanie masy")
    )

    fun defMt(): String {
        val hr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hr < 10 -> "sn"
            hr < 12 -> "ds"
            hr < 16 -> "ob"
            hr < 18 -> "pr"
            else -> "ko"
        }
    }

    fun guessMt(n: String): String {
        if (n.startsWith("Przekąska")) return "pr"
        val breakfastRegex = Regex("^(Owsianka|Omlet|Jajecznica|Naleśniki|Jogurt|Skyr|Shake|Koktajl|Tosty|Granola|Serek|Twarożek|Twaróg|Kanapki|Smoothie)")
        return if (breakfastRegex.containsMatchIn(n)) "sn" else "ob"
    }

    private data class IngData(
        val name: String,
        val kcal: Double,
        val p: Double,
        val c: Double,
        val f: Double,
        val unitWeight: Double? = null
    )

    private val ING = mapOf(
        "ow" to IngData("Płatki owsiane (suche)", 375.0, 13.0, 62.0, 7.0),
        "ry" to IngData("Ryż biały (suchy)", 355.0, 7.0, 78.0, 0.6),
        "mak" to IngData("Makaron (suchy)", 355.0, 12.0, 72.0, 1.5),
        "kas" to IngData("Kasza gryczana (sucha)", 340.0, 12.0, 68.0, 3.0),
        "kus" to IngData("Kuskus (suchy)", 360.0, 13.0, 73.0, 1.5),
        "zi" to IngData("Ziemniaki", 77.0, 2.0, 17.0, 0.1),
        "bat" to IngData("Bataty", 86.0, 1.6, 20.0, 0.1),
        "chl" to IngData("Chleb razowy", 230.0, 8.0, 42.0, 2.0),
        "tor" to IngData("Tortilla pszenna", 300.0, 8.0, 50.0, 7.0, 60.0),
        "kur" to IngData("Pierś z kurczaka", 110.0, 23.0, 0.0, 1.5),
        "ind" to IngData("Pierś z indyka", 105.0, 23.0, 0.0, 1.0),
        "mie" to IngData("Mielony indyk", 150.0, 20.0, 0.0, 8.0),
        "wol" to IngData("Chuda wołowina", 135.0, 21.0, 0.0, 5.0),
        "los" to IngData("Łosoś", 208.0, 20.0, 0.0, 13.0),
        "dor" to IngData("Dorsz", 80.0, 18.0, 0.0, 0.7),
        "tun" to IngData("Tuńczyk w wodzie", 110.0, 25.0, 0.0, 1.0),
        "kre" to IngData("Krewetki", 85.0, 18.0, 1.0, 1.0),
        "jaj" to IngData("Jajka", 143.0, 12.6, 0.7, 9.5, 55.0),
        "sky" to IngData("Skyr", 63.0, 11.0, 4.0, 0.2),
        "twa" to IngData("Chudy twaróg", 100.0, 18.0, 3.5, 0.5),
        "jog" to IngData("Jogurt grecki", 97.0, 9.0, 4.0, 5.0),
        "mle" to IngData("Mleko 2%", 50.0, 3.4, 4.8, 2.0),
        "wie" to IngData("Serek wiejski", 100.0, 12.0, 3.0, 4.0),
        "zol" to IngData("Ser żółty", 350.0, 25.0, 1.0, 28.0),
        "moz" to IngData("Mozzarella light", 170.0, 19.0, 1.0, 10.0),
        "fet" to IngData("Feta", 265.0, 14.0, 4.0, 21.0),
        "oli" to IngData("Oliwa z oliwek", 884.0, 0.0, 0.0, 100.0),
        "mas" to IngData("Masło orzechowe", 600.0, 25.0, 15.0, 50.0),
        "orz" to IngData("Orzechy włoskie", 650.0, 15.0, 14.0, 65.0),
        "mig" to IngData("Migdały", 580.0, 21.0, 22.0, 50.0),
        "chi" to IngData("Nasiona chia", 486.0, 17.0, 42.0, 31.0),
        "mio" to IngData("Miód", 304.0, 0.3, 82.0, 0.0),
        "rod" to IngData("Rodzynki", 300.0, 3.0, 79.0, 0.5),
        "ban" to IngData("Banan", 89.0, 1.1, 23.0, 0.3, 120.0),
        "jab" to IngData("Jabłko", 52.0, 0.3, 14.0, 0.2, 150.0),
        "jag" to IngData("Owoce jagodowe", 45.0, 0.7, 10.0, 0.3),
        "awo" to IngData("Awokado", 160.0, 2.0, 9.0, 15.0, 150.0),
        "bro" to IngData("Brokuł", 34.0, 2.8, 7.0, 0.4),
        "szp" to IngData("Szpinak", 23.0, 2.9, 3.6, 0.4),
        "pom" to IngData("Pomidory", 18.0, 0.9, 3.9, 0.2),
        "ogo" to IngData("Ogórek", 15.0, 0.7, 3.6, 0.1),
        "pap" to IngData("Papryka", 31.0, 1.0, 6.0, 0.3),
        "cuk" to IngData("Cukinia", 17.0, 1.2, 3.0, 0.3),
        "mar" to IngData("Marchew", 41.0, 0.9, 10.0, 0.2),
        "war" to IngData("Warzywa mieszane", 40.0, 2.5, 7.0, 0.5),
        "fas" to IngData("Czerwona fasola z puszki", 90.0, 6.0, 15.0, 0.5),
        "cie" to IngData("Ciecierzyca z puszki", 120.0, 7.0, 18.0, 3.0),
        "soc" to IngData("Soczewica czerwona (sucha)", 340.0, 25.0, 55.0, 1.5),
        "pas" to IngData("Passata pomidorowa", 35.0, 1.6, 6.5, 0.2),
        "kuk" to IngData("Kukurydza z puszki", 85.0, 3.0, 16.0, 1.5),
        "hum" to IngData("Hummus", 230.0, 8.0, 14.0, 17.0),
        "whe" to IngData("Odżywka białkowa", 385.0, 78.0, 7.0, 6.0),
        "tof" to IngData("Tofu", 76.0, 8.0, 2.0, 4.5)
    )

    private val PR = mapOf(
        "ow" to "ugotuj w mleku lub wodzie 5 min",
        "ry" to "ugotuj 12–15 min",
        "mak" to "ugotuj al dente 8–10 min",
        "kas" to "ugotuj 15 min",
        "kus" to "zalej wrzątkiem i przykryj na 5 min",
        "zi" to "ugotuj 15–20 min",
        "bat" to "upiecz w 200°C przez 25 min",
        "tor" to "podgrzej na suchej patelni",
        "kur" to "przypraw i smaż 6–8 min z każdej strony",
        "ind" to "przypraw i smaż 6–8 min z każdej strony",
        "mie" to "zrumień na patelni 6–8 min",
        "wol" to "pokrój i smaż 5–7 min",
        "los" to "piecz w 200°C przez 12–15 min",
        "dor" to "piecz w 200°C przez 12 min",
        "kre" to "smaż 3–4 min",
        "tof" to "pokrój w kostkę i podsmaż 5 min",
        "jaj" to "roztrzep i usmaż 3–4 min albo ugotuj na twardo",
        "tun" to "odcedź",
        "fas" to "odcedź",
        "cie" to "odcedź",
        "kuk" to "odcedź",
        "soc" to "ugotuj 15 min"
    )

    private val VC = setOf("bro", "cuk", "pap", "mar", "war")
    private val VF = setOf("pom", "ogo", "szp", "jab", "ban", "jag", "awo")

    private fun howto(n: String, spec: String): String {
        if (Regex("^(Shake|Koktajl|Smoothie|Przekąska: (smoothie|shake))", RegexOption.IGNORE_CASE).containsMatchIn(n)) {
            return "1. Wszystkie składniki zmiksuj na gładko.\n2. Przelej do szklanki i wypij od razu."
        }
        if (Regex("^(Zupa|Gulasz|Chili|Leczo)|curry", RegexOption.IGNORE_CASE).containsMatchIn(n)) {
            return "1. Pokrój mięso, tofu i warzywa w kostkę.\n2. Zrumień mięso lub tofu w garnku na łyżce oleju przez 5 min.\n3. Dodaj pozostałe składniki i 400 ml wody lub bulionu.\n4. Gotuj pod przykryciem 20–25 min, dopraw solą, pieprzem i ziołami."
        }
        val st = mutableListOf<String>()
        val vc = mutableListOf<String>()
        val vf = mutableListOf<String>()
        val seen = mutableSetOf<String>()
        val pl = n.startsWith("Placki")
        val sp = when {
            n.startsWith("Naleśniki") -> "Zblenduj płatki z jajkami i dodatkami na ciasto, smaż małe placki 2–3 min z każdej strony."
            pl -> "Warzywa zetrzyj, wymieszaj z jajkami i płatkami, smaż placki 3 min z każdej strony."
            Regex("^(Kotlety|Burger)").containsMatchIn(n) -> "Mięso wymieszaj z jajkiem i płatkami owsianymi, dopraw i uformuj kotlety; smaż po 5–6 min z każdej strony."
            else -> ""
        }
        if (sp.isNotEmpty()) st.add(sp)

        spec.split(",").forEach { z ->
            val match = Regex("^[a-z]+").find(z) ?: return@forEach
            val c = match.value
            val ing = ING[c] ?: return@forEach
            if (seen.contains(c)) return@forEach
            seen.add(c)
            if (sp.isNotEmpty() && (c == "ow" || c == "jaj" || c == "mie" || (pl && (c == "zi" || VC.contains(c))))) return@forEach
            if (PR.containsKey(c)) {
                st.add("${ing.name}: ${PR[c]}.")
            } else if (VC.contains(c)) {
                vc.add(ing.name.lowercase())
            } else if (VF.contains(c)) {
                vf.add(ing.name.lowercase())
            }
        }
        if (vc.isNotEmpty()) st.add("Warzywa (${vc.joinToString(", ")}): pokrój i podsmaż lub ugotuj 5–7 min.")
        if (vf.isNotEmpty()) st.add("Pokrój na świeżo: ${vf.joinToString(", ")}.")

        val ending = when {
            Regex("bowl|^Sałatka", RegexOption.IGNORE_CASE).containsMatchIn(n) -> "Złóż w misce, polej oliwą lub jogurtem i dopraw solą oraz pieprzem."
            Regex("^(Wrap|Burrito|Kebab|Tortilla)").containsMatchIn(n) -> "Całość zawiń w tortillę, możesz opiec ją 2 min na patelni."
            Regex("^(Kanapki|Tosty|Przekąska: kanapk)").containsMatchIn(n) -> "Ułóż składniki na pieczywie i podawaj."
            n.startsWith("Pizza") -> "Posmaruj tortillę sosem, obłóż, posyp serem i piecz w 220°C przez 8–10 min."
            Regex("^(Zapiekanka|Lasagne)").containsMatchIn(n) -> "Przełóż do formy, posyp serem i zapiekaj w 200°C przez 20 min."
            else -> "Połącz składniki, dopraw do smaku i podawaj."
        }
        st.add(ending)
        return st.mapIndexed { idx, x -> "${idx + 1}. $x" }.joinToString("\n")
    }

    private fun gen(s: String): List<Recipe> {
        return s.split(";").mapNotNull { r ->
            val a = r.split("|")
            if (a.size < 2) return@mapNotNull null
            var k = 0.0
            var p = 0.0
            var c = 0.0
            var f = 0.0
            val t = mutableListOf<String>()

            a[1].split(",").forEach { z ->
                val m = Regex("^([a-z]+)(\\d+)$").find(z) ?: return@forEach
                val code = m.groupValues[1]
                val g = m.groupValues[2].toDoubleOrNull() ?: 0.0
                val d = ING[code] ?: return@forEach
                val x = g / 100.0
                k += d.kcal * x
                p += d.p * x
                c += d.c * x
                f += d.f * x

                val unitStr = if (d.unitWeight != null) {
                    val count = ((g / d.unitWeight) * 2).roundToInt() / 2.0
                    " (≈${count.toString().replace('.', ',')} szt.)"
                } else ""
                t.add("${g.roundToInt()} g ${d.name.lowercase()}$unitStr")
            }

            Recipe(
                n = a[0],
                k = k.roundToInt(),
                p = p.roundToInt(),
                c = c.roundToInt(),
                f = f.roundToInt(),
                i = t.joinToString(", "),
                s = howto(a[0], a[1])
            )
        }
    }

    private const val R1 = "Owsianka z jagodami i skyrem|ow40,sky150,jag80;Omlet z warzywami|jaj165,pap60,pom60,szp30,oli5;Jajecznica ze szpinakiem na chlebie|jaj165,szp60,oli5,chl40;Naleśniki owsiane z twarogiem|ow50,jaj55,twa120,jab75;Skyr z owocami i chia|sky200,jag100,chi10,ban60;Tosty z awokado i jajkiem|chl60,awo75,jaj55;Serek wiejski z pomidorami|wie200,pom150,chl40;Shake proteinowy z bananem|whe30,ban120,mle250;Kurczak z ryżem i brokułem|kur150,ry50,bro150,oli5;Indyk z kaszą i warzywami|ind150,kas50,war150;Kurczak curry z cukinią|kur150,cuk150,pas100,ry40,oli5;Sałatka z kurczakiem i fetą|kur120,fet30,pom100,ogo100,szp40,oli8;Sałatka z tuńczykiem i jajkiem|tun120,jaj55,ogo100,pom100,oli8;Zupa pomidorowa z indykiem|pas250,ind100,ry30,mar80;Chili con carne light|mie100,fas120,pas150,pap80;Dorsz z batatem i szpinakiem|dor180,bat200,szp100,oli8;Łosoś z brokułem i ziemniakami|los120,bro200,zi150;Krewetki z cukinią i makaronem|kre150,cuk200,mak50,oli8;Tofu z warzywami i ryżem|tof150,war200,ry40,oli8;Leczo z indykiem i kaszą|ind150,pap150,cuk100,pas100,kas30;Gulasz wołowy z warzywami|wol150,mar100,pas100,zi150,oli5;Burrito bowl z kurczakiem|kur130,ry40,fas80,kuk50,pom80;Zapiekanka z cukinii z mozzarellą|cuk300,moz60,mie80,pas100;Wrap z kurczakiem i warzywami|tor60,kur100,ogo60,pom60,jog30;Makaron z indykiem i szpinakiem|mak60,mie100,szp80,pas100;Pasta z ciecierzycy z warzywami|cie150,hum30,pap100,ogo100,chl40;Sałatka z ciecierzycą i fetą|cie130,fet30,ogo100,pom100,oli5;Zupa krem z brokułu|bro300,zi100,jog50,fet20;Zupa z soczewicy|soc50,mar100,zi100,pas100,oli5;Kurczak teriyaki z ryżem|kur150,ry50,war150,mio10;Pieczony kurczak z batatami|kur150,bat200,bro100,oli5;Dorsz z kuskusem i cukinią|dor170,kus50,cuk100;Kotlety z indyka z kaszą|mie120,ow20,jaj30,kas40,ogo100;Szakszuka|jaj110,pas200,pap80,fet20,chl40;Frittata z cukinią|jaj165,cuk150,moz40,pom50;Twarożek z ogórkiem i chlebem|twa200,ogo80,chl60;Placki z cukinii|cuk200,jaj55,ow30,jog50;Kanapki z tuńczykiem|chl80,tun80,ogo80,jog30;Sałatka z krewetkami i awokado|kre120,awo75,ogo100,pom100,oli5;Owsianka z jabłkiem i mlekiem|ow50,mle200,jab150;Jogurt z orzechami i jabłkiem|jog200,orz15,jab150;Przekąska: hummus z warzywami|hum50,mar100,pap100,ogo100;Przekąska: serek wiejski z owocami|wie150,jag100;Przekąska: jajka z warzywami|jaj110,pap100,ogo100;Przekąska: smoothie ze szpinakiem|szp50,ban120,jog100,mle100;Przekąska: skyr z masłem orzechowym|sky200,mas10;Kasza gryczana z kurczakiem i brokułem|kas50,kur130,bro150;Ryż z tuńczykiem i kukurydzą|ry50,tun100,kuk60,pap80;Pizza na tortilli z kurczakiem|tor60,pas50,moz50,kur80;Zupa jarzynowa z fasolą|war250,fas100,zi100,oli5"
    private const val R2 = "Owsianka z masłem orzechowym i bananem|ow100,mle300,ban120,mas20;Owsianka z orzechami i rodzynkami|ow90,mle300,orz25,rod30,mio15;Naleśniki bananowe|ow80,jaj110,ban120,mas15,mle100;Jajecznica z 4 jajek na toscie|jaj220,chl100,zol30,oli5;Omlet z kurczakiem i serem|jaj165,kur80,zol40,pap60,chl80;Koktajl na masę|mle400,ban120,ow50,whe30,mas20;Shake gainer|mle400,ban120,ow70,whe40,mio20;Kanapki z jajkiem i awokado|chl120,awo100,jaj165,zol20;Granola z jogurtem greckim|ow80,jog250,mig30,mio20,jab100;Skyr z owsianką i masłem orzechowym|sky250,ow60,mas25,ban120;Makaron z kurczakiem w sosie pomidorowym|mak110,kur150,pas150,oli10,zol20;Makaron bolognese z indykiem|mak110,mie150,pas150,oli10,zol20;Spaghetti z wołowiną|mak110,wol150,pas150,oli10;Ryż z kurczakiem i warzywami|ry100,kur180,war150,oli10;Kurczak curry z ryżem|ry100,kur180,pas100,jog50,oli10;Burrito z kurczakiem|tor120,kur150,ry50,fas60,zol30,pom50;Burrito z wołowiną|tor120,wol130,ry50,fas60,zol30;Kebab z kurczakiem w tortilli|tor120,kur170,jog60,hum30,pom60,ogo60;Łosoś z ziemniakami i warzywami|los160,zi350,war150,oli10;Dorsz z ryżem i warzywami|dor200,ry100,war150,oli15;Krewetki z makaronem|kre200,mak110,pas100,oli15;Wołowina z batatami i brokułem|wol180,bat350,bro150,oli10;Kasza z mielonym indykiem|kas100,mie180,war150,oli10;Kotlety z indyka z ziemniakami|mie200,jaj55,ow30,zi350,oli10,ogo100;Gulasz wołowy z kaszą|wol200,kas90,mar100,pas100,oli10;Chili con carne z ryżem|mie150,fas150,pas200,ry80,zol30;Leczo z kurczakiem i kaszą|kur180,pap150,cuk100,pas100,kas80,oli10;Zapiekanka makaronowa z mozzarellą|mak110,mie150,moz80,pas150;Lasagne uproszczone|mak100,mie150,pas150,moz80,jog60;Pizza na tortilli z mięsem i serem|tor120,pas60,moz80,kur120,pap50,oli5;Wrap z tuńczykiem i serem|tor120,tun120,zol30,hum40,pom60;Ryż z jajkiem i tofu|ry100,jaj110,tof120,war150,oli10;Kuskus z kurczakiem i hummusem|kus100,kur180,hum50,ogo100,oli10;Makaron z tuńczykiem i fetą|mak110,tun120,pom150,oli15,fet30;Ziemniaki z jajkiem i twarogiem|zi400,jaj110,twa150,oli10;Placki ziemniaczane z jogurtem|zi400,jaj55,ow40,jog100,oli15;Bataty zapiekane z kurczakiem i serem|bat400,kur180,moz60,oli10;Zupa gulaszowa z wołowiną|wol150,zi250,pap100,pas100,chl80;Zupa krem z batatów z kurczakiem|bat300,kur120,jog60,chl80,oli10;Tosty francuskie z bananem|chl150,jaj165,mle100,mio20,ban120;Bowl z kurczakiem, ryżem i awokado|ry90,kur150,awo100,kuk60,fas60;Bowl z łososiem i kuskusem|kus90,los130,awo75,ogo100,oli5;Bowl z wołowiną i batatami|bat300,wol150,awo75,pap100,oli10;Bowl z tofu i orzechami|ry90,tof180,orz20,war150,mas15;Owsianka białkowa z orzechami|ow90,mle300,whe30,jab150,orz15;Przekąska: kanapki z masłem orzechowym i bananem|chl100,mas30,ban120;Przekąska: jogurt z granolą i orzechami|jog250,ow60,orz30,mio15;Przekąska: shake z masłem orzechowym|mle400,ban120,mas30,whe30;Przekąska: hummus z kurczakiem i chlebem|hum80,kur120,chl100;Przekąska: twaróg z orzechami i miodem|twa250,orz30,mio20,jab150"
    private const val R3 = "Owsianka z bananem i orzechami|ow60,mle250,ban120,orz10;Jajecznica z pomidorem na chlebie|jaj165,pom100,chl70,oli5;Omlet z serem i szpinakiem|jaj165,zol25,szp50,chl60;Jogurt z granolą i owocami|jog200,ow40,jag80,mio10;Skyr z owsianką i jabłkiem|sky200,ow40,jab150,orz10;Kanapki z twarogiem i warzywami|chl100,twa150,ogo80,pom80;Tosty z awokado i jajkiem|chl90,awo100,jaj110;Naleśniki owsiane z jogurtem i bananem|ow60,jaj55,jog100,ban120;Shake z bananem i masłem orzechowym|mle300,ban120,mas15,whe20;Kurczak z ryżem i warzywami|kur150,ry70,war150,oli8;Kurczak z kaszą gryczaną i surówką|kur150,kas60,mar100,ogo100,oli8;Indyk z batatami i brokułem|ind150,bat250,bro150,oli5;Makaron z kurczakiem i szpinakiem|mak80,kur130,szp80,pas100,oli8;Makaron z tuńczykiem i pomidorami|mak80,tun100,pom150,oli8,fet20;Spaghetti z indykiem|mak80,mie120,pas150,oli5;Łosoś z ziemniakami i fasolką|los130,zi250,war120,oli5;Dorsz z ryżem i warzywami|dor180,ry70,war150,oli8;Krewetki z makaronem i cukinią|kre150,mak70,cuk150,oli8;Wołowina z kuskusem i papryką|wol140,kus70,pap150,oli5;Gulasz wołowy z kaszą|wol150,kas60,mar100,pas100,oli5;Chili con carne|mie130,fas120,pas150,ry50,pap60;Leczo z indykiem i ryżem|ind130,pap150,cuk100,pas100,ry50,oli8;Burrito z kurczakiem|tor60,kur120,ry40,fas50,zol20,pom50;Wrap z tuńczykiem i serem|tor60,tun100,hum30,ogo80,pom60,zol20;Wrap z kurczakiem i hummusem|tor60,kur120,hum40,ogo80,pom60,jog20;Kebab z indyka w tortilli|tor60,ind130,jog50,pom60,ogo60,hum20;Zapiekanka z batatów z kurczakiem|bat300,kur130,moz50,oli5;Zapiekanka makaronowa z indykiem|mak70,mie120,moz50,pas120;Szakszuka z fetą i chlebem|jaj165,pas200,pap80,fet30,chl60;Frittata z warzywami i serem|jaj165,cuk150,moz40,pom60,chl50;Ziemniaki z jajkiem i twarogiem|zi300,jaj110,twa120,oli5;Placki ziemniaczane z jogurtem|zi250,jaj55,ow30,jog80,oli10;Zupa krem z batatów z kurczakiem|bat250,kur100,jog40,chl60,oli5;Zupa pomidorowa z ryżem i indykiem|pas300,ind100,ry40,mar80,jog30;Zupa z soczewicy z chlebem|soc60,mar100,zi100,pas100,chl50,oli5;Zupa gulaszowa|wol100,zi200,pap100,pas100,chl40;Sałatka z kurczakiem, fetą i chlebem|kur130,fet30,pom100,ogo100,szp40,chl60,oli8;Sałatka z tuńczykiem i fasolą|tun120,fas100,jaj55,ogo100,pom100,oli10,chl40;Sałatka z ciecierzycą i fetą|cie150,fet40,ogo100,pom100,oli10,chl50;Bowl z kurczakiem i ryżem|ry60,kur130,awo75,kuk50,war100;Bowl z łososiem i kuskusem|kus60,los110,ogo100,awo50,oli5;Bowl z wołowiną i batatami|bat200,wol120,pap100,awo50;Bowl z tofu i ryżem|ry60,tof150,war150,mas10;Pizza na tortilli z kurczakiem|tor60,pas50,moz60,kur90,pap50;Kanapki z jajkiem i awokado|chl100,awo75,jaj110;Twaróg z owocami i orzechami|twa200,jag100,orz15,mio10;Przekąska: jogurt z orzechami i miodem|jog200,orz15,mio10;Przekąska: hummus z warzywami i chlebem|hum60,chl50,mar100,pap100;Przekąska: kanapka z masłem orzechowym i bananem|chl70,mas20,ban120;Przekąska: skyr z owocami i chia|sky200,jag100,chi10,ban60,orz10"
    private const val R4 = "Owsianka z cynamonem i skyrem|ow40,sky150,jab100;Omlet z papryką i mozzarellą|jaj110,pap80,moz30,szp30;Naleśniki z twarogiem i jagodami|ow40,jaj55,twa100,jag80;Jajka sadzone na chlebie z awokado|jaj110,chl50,awo50;Kanapki z serkiem wiejskim|chl80,wie100,ogo80,pom80;Smoothie z jagodami i skyrem|jag100,sky150,mle150,ban60;Kurczak z kuskusem i warzywami|kur150,kus50,war150,oli5;Indyk z kaszą i szpinakiem|ind150,kas50,szp100;Sałatka z kurczakiem i awokado|kur120,awo75,pom100,ogo100,szp40,oli5;Sałatka z krewetkami i ciecierzycą|kre120,cie80,ogo100,pom100,oli8;Dorsz z ziemniakami i brokułem|dor170,zi200,bro150,oli5;Łosoś z ryżem i szpinakiem|los110,ry40,szp100;Zapiekany kurczak z cukinią i mozzarellą|kur140,cuk200,moz40,pas80;Makaron z krewetkami i szpinakiem|mak50,kre120,szp80,pas80,oli5;Tofu curry z warzywami|tof150,war200,pas100,ry30,oli5;Kotlety z indyka z surówką|mie120,jaj30,ow20,mar100,ogo100,oli5;Zupa z ciecierzycy i warzyw|cie120,war200,pas100,oli5;Zupa krem z cukinii|cuk400,zi100,jog50,fet20;Chili z indykiem i fasolą|mie100,fas100,pas200,pap80,kuk40;Gulasz z indyka z warzywami|ind150,mar100,pap100,pas100,zi100;Wrap z jajkiem i warzywami|tor60,jaj110,ogo80,pom60,jog20;Tosty z tuńczykiem i ogórkiem|chl80,tun100,ogo80,jog30;Bowl z ciecierzycą i hummusem|cie120,hum30,ogo100,pom100,pap80,ry20;Przekąska: jogurt z jagodami|jog150,jag100;Przekąska: twaróg z ogórkiem|twa150,ogo100,pom50"
    private const val R5 = "Owsianka z jogurtem, bananem i orzechami|ow90,jog150,mle200,ban120,orz20;Naleśniki z twarogiem i miodem|ow80,jaj110,twa150,mio20,ban120;Omlet z 4 jajek z serem i chlebem|jaj220,zol40,pap60,chl100,oli5;Kanapki z masłem orzechowym i bananem|chl150,mas40,ban120;Smoothie bowl z granolą|jog200,ban120,ow60,mig20,jag100,mio15;Shake po treningu z owsianką|mle400,ow60,ban120,whe35,mas15;Kurczak z ryżem i sosem pomidorowym|kur200,ry100,pas150,oli10,zol20;Makaron z łososiem i szpinakiem|mak110,los130,szp100,pas100,oli10;Wołowina z ryżem i warzywami|wol180,ry100,war150,oli10;Kotlety z indyka z kaszą i surówką|mie180,jaj55,ow30,kas80,mar80,oli5;Burrito bowl z wołowiną|wol150,ry100,fas80,kuk50,awo75,zol30;Kebab z kurczaka z hummusem|tor120,kur180,hum50,jog60,pom60,ogo60;Pizza na tortilli z wołowiną i serem|tor120,pas60,moz80,wol100,pap60,oli5;Curry z kurczaka z ryżem|kur180,ry100,pas150,jog60,oli15;Gulasz wołowy z ziemniakami|wol200,zi350,mar100,pas100,oli10;Chili z wołowiną i ryżem|wol150,fas120,pas200,ry80,zol30,pap60;Zupa gulaszowa z chlebem|wol150,zi250,pap100,pas100,chl100;Zapiekanka z batatów i wołowiny|bat400,wol150,moz70,oli5;Lasagne z indykiem|mak110,mie150,pas150,moz70,jog60;Ryż z tuńczykiem i jajkiem|ry100,tun120,jaj110,kuk60,oli10;Dorsz z ziemniakami i fasolką|dor220,zi400,war150,oli20;Łosoś z kuskusem i awokado|los150,kus90,awo75,ogo100,oli5;Makaron z mielonym indykiem i serem|mak120,mie150,pas150,zol30,oli10;Przekąska: kanapki z jajkiem i serem|chl120,jaj110,zol30;Przekąska: shake bananowo-orzechowy|mle400,ban120,mas30,ow30"
    private const val R6 = "Owsianka z jabłkiem i orzechami|ow60,mle250,jab150,orz10;Jajecznica z awokado i chlebem|jaj165,awo50,chl70;Naleśniki owsiane z jagodami|ow60,jaj55,jog80,jag80;Kanapki z indykiem i hummusem|chl100,ind80,hum30,ogo80,pom80;Skyr z granolą i bananem|sky200,ow40,ban120,mig10;Shake białkowy z owsianką|mle300,ow40,ban100,whe25;Kurczak z ryżem i szpinakiem|kur150,ry70,szp100,oli8;Indyk z batatami i fasolką|ind150,bat250,war120,oli5;Makaron z kurczakiem i brokułem|mak80,kur120,bro150,oli8,zol15;Łosoś z kaszą i warzywami|los110,kas60,war150;Dorsz z kuskusem i papryką|dor180,kus70,pap150,oli8;Krewetki z ryżem i warzywami|kre150,ry70,war150,oli8;Wołowina z ziemniakami i brokułem|wol140,zi250,bro150,oli5;Burger z indyka z surówką|mie150,jaj30,ow20,chl60,ogo80,pom60;Zapiekanka z cukinii i indyka|cuk250,mie130,moz50,pas100,ry30;Leczo z kurczakiem|kur150,pap150,cuk150,pas100,ry40,oli8;Zupa pomidorowa z kaszą i kurczakiem|pas300,kur100,kas40,mar80,chl40;Zupa z fasolą i warzywami|fas120,war200,zi100,pas100,chl40,oli5;Sałatka z łososiem i ziemniakami|los100,zi150,ogo100,pom100,szp40,oli8;Sałatka z kurczakiem i makaronem|kur120,mak50,pom100,ogo100,fet25,oli8;Wrap z indykiem i awokado|tor60,ind100,awo50,pom60,ogo60,jog30;Tortilla z jajkiem i fasolą|tor60,jaj110,fas60,zol20,pom60;Bowl z tuńczykiem, ryżem i awokado|ry60,tun100,awo60,kuk40,ogo80;Przekąska: jogurt z granolą|jog200,ow30,jab100,mio10;Przekąska: twaróg z owocami|twa200,jag100,ban60,orz10"

    val RECIPES_BY_CAT: Map<String, List<Recipe>> by lazy {
        val baseRed = mutableListOf(
            Recipe(n = "Owsianka proteinowa z jabłkiem", k = 360, p = 23, c = 59, f = 4, i = "50 g płatków owsianych, 150 g skyru, 1 jabłko, cynamon", s = "Zalej płatki gorącą wodą na 5 minut, wmieszaj skyr, dodaj pokrojone jabłko i cynamon."),
            Recipe(n = "Omlet ze szpinakiem i fetą", k = 270, p = 24, c = 3, f = 16, i = "2 jajka, 2 białka, garść szpinaku, 30 g fety", s = "Roztrzep jajka z białkami, wlej na patelnię, dodaj szpinak i fetę, smaż pod przykryciem 4 minuty."),
            Recipe(n = "Kurczak z ryżem i brokułem", k = 475, p = 43, c = 55, f = 8, i = "150 g piersi z kurczaka, 60 g ryżu (suchego), 150 g brokułu, 1 łyżeczka oleju", s = "Ugotuj ryż, kurczaka smaż 6–8 minut z każdej strony, brokuł gotuj na parze 5 minut."),
            Recipe(n = "Sałatka z tuńczykiem i fasolą", k = 350, p = 35, c = 22, f = 12, i = "1 puszka tuńczyka w wodzie, 100 g czerwonej fasoli z puszki, ogórek, pomidor, 1 łyżka oliwy", s = "Odcedź tuńczyka i fasolę, pokrój warzywa, wymieszaj z oliwą, solą i pieprzem."),
            Recipe(n = "Twaróg z warzywami i chlebem", k = 380, p = 40, c = 40, f = 4, i = "200 g chudego twarogu, rzodkiewki, ogórek, szczypiorek, 2 kromki chleba razowego", s = "Rozgnieć twaróg widelcem, wymieszaj z pokrojonymi warzywami i podaj z chlebem.")
        )
        baseRed.addAll(gen(R1))
        baseRed.addAll(gen(R4))

        val baseUtr = mutableListOf<Recipe>()
        baseUtr.addAll(gen(R3))
        baseUtr.addAll(gen(R6))

        val baseMasa = mutableListOf(
            Recipe(n = "Owsianka z masłem orzechowym i bananem", k = 725, p = 28, c = 110, f = 20, i = "100 g płatków owsianych, 300 ml mleka 2%, 1 banan, 1 łyżka masła orzechowego", s = "Gotuj płatki w mleku 5 minut, dodaj pokrojonego banana i masło orzechowe."),
            Recipe(n = "Makaron z mielonym indykiem", k = 780, p = 50, c = 80, f = 28, i = "100 g makaronu (suchego), 150 g mielonego indyka, 150 g passaty, 1 łyżka oliwy, 20 g sera", s = "Zrumień mięso na oliwie, wlej passatę i duś 10 minut, wymieszaj z makaronem i posyp serem."),
            Recipe(n = "Burrito z kurczakiem i ryżem", k = 755, p = 54, c = 93, f = 16, i = "1 duża tortilla, 150 g kurczaka, 60 g ryżu (suchego), 60 g fasoli, 30 g sera, salsa", s = "Podsmaż kurczaka, zwiń wszystko w tortillę i opiecz na patelni po 2 minuty z każdej strony."),
            Recipe(n = "Koktajl kaloryczny", k = 610, p = 44, c = 72, f = 18, i = "300 ml mleka, 1 banan, 40 g płatków owsianych, 30 g odżywki białkowej, 1 łyżka masła orzechowego", s = "Zmiksuj wszystko na gładko. Dobry po treningu albo jako dodatkowy posiłek."),
            Recipe(n = "Łosoś z ziemniakami i fasolką", k = 675, p = 39, c = 60, f = 30, i = "150 g łososia, 300 g ziemniaków, 150 g fasolki szparagowej, 1 łyżka oliwy", s = "Ziemniaki gotuj 20 minut, łososia piecz w 200°C przez 12–15 minut, fasolkę gotuj 6 minut i polej oliwą.")
        )
        baseMasa.addAll(gen(R2))
        baseMasa.addAll(gen(R5))

        // Procedural generator
        data class GP(val p0: String, val p1: String, val p2: String, val p3: Double)
        val gps = listOf(
            GP("Kurczak", "kurczakiem", "kur", 150.0),
            GP("Indyk", "indykiem", "ind", 150.0),
            GP("Mielony indyk", "mielonym indykiem", "mie", 130.0),
            GP("Wołowina", "wołowiną", "wol", 130.0),
            GP("Łosoś", "łososiem", "los", 120.0),
            GP("Dorsz", "dorszem", "dor", 170.0),
            GP("Tuńczyk", "tuńczykiem", "tun", 120.0),
            GP("Krewetki", "krewetkami", "kre", 150.0),
            GP("Tofu", "tofu", "tof", 160.0),
            GP("Jajecznica", "jajkami", "jaj", 165.0)
        )
        val gcs = listOf(
            "ryżem" to "ry",
            "makaronem" to "mak",
            "kaszą gryczaną" to "kas",
            "kuskusem" to "kus",
            "ziemniakami" to "zi",
            "batatami" to "bat",
            "tortillą" to "tor",
            "chlebem razowym" to "chl",
            "soczewicą" to "soc"
        )
        data class GV(val name: String, val code: String, val weight: Double)
        val gvs = listOf(
            GV("brokułem", "bro", 150.0),
            GV("szpinakiem", "szp", 100.0),
            GV("cukinią", "cuk", 200.0),
            GV("papryką", "pap", 120.0),
            GV("marchewką", "mar", 100.0),
            GV("warzywami", "war", 200.0),
            GV("pomidorami", "pom", 150.0),
            GV("ogórkiem", "ogo", 150.0)
        )
        data class GS(val name: String, val code: String, val weights: List<Double>)
        val gss = listOf(
            GS("oliwą z oliwek", "oli", listOf(5.0, 8.0, 12.0)),
            GS("hummusem", "hum", listOf(30.0, 40.0, 60.0)),
            GS("jogurtem greckim", "jog", listOf(40.0, 60.0, 100.0)),
            GS("fetą", "fet", listOf(25.0, 30.0, 40.0)),
            GS("żółtym serem", "zol", listOf(20.0, 30.0, 40.0)),
            GS("mozzarellą", "moz", listOf(40.0, 60.0, 80.0)),
            GS("awokado", "awo", listOf(50.0, 75.0, 100.0)),
            GS("passatą pomidorową", "pas", listOf(100.0, 120.0, 150.0))
        )

        val totalCombinations = gps.size * gcs.size * gvs.size * gss.size
        val targets = listOf(420.0, 550.0, 780.0)
        val multipliers = listOf(1.0, 1.1, 1.25)
        val generatedOut = listOf(mutableListOf<String>(), mutableListOf<String>(), mutableListOf<String>())
        val per = 5000 / 3
        val cnt = intArrayOf(0, 0, 0)

        for (idx in 0 until totalCombinations) {
            if (cnt[0] >= per && cnt[1] >= per && cnt[2] >= per) break
            val cat = idx % 3
            if (cnt[cat] >= per) continue
            val j = (idx * 7919) % totalCombinations
            val pIdx = j / (gcs.size * gvs.size * gss.size)
            val cIdx = (j / (gvs.size * gss.size)) % gcs.size
            val vIdx = (j / gss.size) % gvs.size
            val sIdx = j % gss.size

            val pObj = gps[pIdx]
            val cObj = gcs[cIdx]
            val vObj = gvs[vIdx]
            val sObj = gss[sIdx]

            val pg = ((pObj.p3 * multipliers[cat]) / 5.0).roundToInt() * 5
            val sg = sObj.weights[cat].roundToInt()
            val nc = (ING[pObj.p2]!!.kcal * pg) / 100.0 +
                    (ING[vObj.code]!!.kcal * vObj.weight) / 100.0 +
                    (ING[sObj.code]!!.kcal * sg) / 100.0
            val tgt = targets[cat] + (((idx * 37) % 5) - 2) * 35
            val carbKcal = ING[cObj.second]!!.kcal
            var cg = ((tgt - nc) / carbKcal) * 100.0
            cg = when (cObj.second) {
                "tor" -> max(60.0, (cg / 60.0).roundToInt() * 60.0)
                "chl" -> min(200.0, max(40.0, (cg / 5.0).roundToInt() * 5.0))
                else -> min(450.0, max(20.0, (cg / 5.0).roundToInt() * 5.0))
            }

            val nm = if (cObj.second == "tor") {
                "Wrap z ${pObj.p1}, ${vObj.name} i ${sObj.name}"
            } else {
                "${pObj.p0} z ${cObj.first}, ${vObj.name} i ${sObj.name}"
            }

            generatedOut[cat].add("$nm|${pObj.p2}$pg,${cObj.second}${cg.roundToInt()},${vObj.code}${vObj.weight.roundToInt()},${sObj.code}$sg")
            cnt[cat]++
        }

        baseRed.addAll(gen(generatedOut[0].joinToString(";")))
        baseUtr.addAll(gen(generatedOut[1].joinToString(";")))
        baseMasa.addAll(gen(generatedOut[2].joinToString(";")))

        mapOf(
            "Redukcja" to baseRed,
            "Utrzymanie" to baseUtr,
            "Masa" to baseMasa
        )
    }

    val WORKOUT_PLANS: Map<String, List<PlanDay>> by lazy {
        val map = mutableMapOf<String, List<PlanDay>>()
        map["FBW 3 dni"] = listOf(
            PlanDay("Dzień A", listOf(
                PlanExercise("Przysiad ze sztangą", "3×8"),
                PlanExercise("Wyciskanie leżąc", "3×8"),
                PlanExercise("Wiosłowanie sztangą", "3×10"),
                PlanExercise("Wyciskanie nad głowę", "3×10"),
                PlanExercise("Uginanie ramion", "2×12")
            )),
            PlanDay("Dzień B", listOf(
                PlanExercise("Martwy ciąg", "3×5"),
                PlanExercise("Wyciskanie na skosie", "3×10"),
                PlanExercise("Podciąganie", "3×6–8"),
                PlanExercise("Wypychanie na suwnicy", "3×12"),
                PlanExercise("Dipy", "2×10")
            ))
        )
        map["Push/Pull/Legs"] = listOf(
            PlanDay("Push", listOf(
                PlanExercise("Wyciskanie leżąc", "4×6–8"),
                PlanExercise("Wyciskanie nad głowę", "3×8"),
                PlanExercise("Wyciskanie na skosie", "3×10"),
                PlanExercise("Dipy", "3×10")
            )),
            PlanDay("Pull", listOf(
                PlanExercise("Martwy ciąg", "3×5"),
                PlanExercise("Podciąganie", "4×6–10"),
                PlanExercise("Wiosłowanie sztangą", "3×8"),
                PlanExercise("Uginanie ramion", "3×12")
            )),
            PlanDay("Legs", listOf(
                PlanExercise("Przysiad ze sztangą", "4×6–8"),
                PlanExercise("Martwy ciąg rumuński", "3×8"),
                PlanExercise("Wypychanie na suwnicy", "3×10"),
                PlanExercise("Wspięcia na palce", "4×12")
            ))
        )

        val rawPlans = "Upper/Lower 4 dni=Góra A:Wyciskanie sztangi leżąc@4×6–8,Wiosłowanie sztangą@4×8,Wyciskanie nad głowę@3×8,Podciąganie nachwytem@3×6–10,Uginanie ramion ze sztangą@2×10,Prostowanie ramion z liną@2×12;Dół A:Przysiad ze sztangą@4×6–8,Martwy ciąg rumuński@3×8,Wypychanie na suwnicy@3×10,Uginanie nóg leżąc@3×12,Wspięcia na palce stojąc@4×12;Góra B:Wyciskanie sztangi na skosie@4×8,Ściąganie drążka wyciągu górnego@4×10,Wyciskanie hantli siedząc@3×10,Wiosłowanie na wyciągu siedząc@3×10,Unoszenie hantli bokiem@3×15,Uginanie młotkowe@2×12;Dół B:Martwy ciąg@3×5,Przysiad bułgarski@3×8,Hip thrust@3×10,Prostowanie nóg na maszynie@3×12,Plank@3×45 s#FBW dla początkujących=Dzień A:Przysiad goblet@3×10,Wyciskanie hantli leżąc@3×10,Ściąganie drążka wyciągu górnego@3×10,Wyciskanie hantli siedząc@2×10,Plank@3×30 s;Dzień B:Wypychanie na suwnicy@3×12,Pompki@3×8–12,Wiosłowanie na wyciągu siedząc@3×10,Martwy ciąg rumuński@3×10,Unoszenie kolan w zwisie@2×10;Dzień C:Przysiad ze sztangą@3×8,Wyciskanie na maszynie@3×10,Wiosłowanie hantlą jedno-rącz@3×10,Hip thrust@3×12,Rowerek@3×15#Split 5 dni=Klatka:Wyciskanie sztangi leżąc@4×6–8,Wyciskanie hantli na skosie@3×10,Rozpiętki na maszynie butterfly@3×12,Krzyżowanie linek wyciągu@3×15,Dipy na poręczach@3×10;Plecy:Martwy ciąg@3×5,Podciąganie nachwytem@4×6–10,Wiosłowanie sztangą@3×8,Ściąganie wąskim uchwytem@3×10,Face pull@3×15;Nogi:Przysiad ze sztangą@4×6–8,Wypychanie na suwnicy@3×10,Martwy ciąg rumuński@3×8,Uginanie nóg siedząc@3×12,Wspięcia na palce stojąc@4×12;Barki:Wyciskanie nad głowę@4×8,Unoszenie hantli bokiem@4×15,Unoszenie hantli w opadzie@3×15,Szrugsy z hantlami@3×12,Odwrotne rozpiętki na maszynie@3×15;Ręce:Uginanie ramion ze sztangą łamaną@3×10,Uginanie młotkowe@3×12,Wyciskanie francuskie@3×10,Prostowanie ramion z liną@3×12,Uginanie na modlitewniku@2×12#Trening w domu=Dzień A:Pompki@4×10–15,Przysiad goblet@4×12,Wykroki z hantlami@3×10,Plank@3×45 s,Dead bug@3×10;Dzień B:Pompki diamentowe@3×10,Wiosłowanie hantlą jedno-rącz@4×10,Hip thrust@4×12,Unoszenie hantli bokiem@3×15,Rowerek@3×20;Dzień C:Przysiad bułgarski@3×10,Dipy na ławce@3×12,Uginanie ramion z hantlami@3×12,Wspinaczka@3×30 s,Burpees@3×10#Redukcja: siła + cardio=Siła A:Przysiad ze sztangą@3×8,Wyciskanie sztangi leżąc@3×8,Wiosłowanie sztangą@3×10,Plank@3×45 s;Siła B:Martwy ciąg rumuński@3×8,Wyciskanie nad głowę@3×8,Podciąganie nachwytem@3×8,Wykroki chodzone@3×12;Cardio:Marsz pod górę na bieżni@1×30 min,Interwały na rowerze@8×30 s,Wioślarz@1×10 min#Minimum 2 dni=Dzień 1:Przysiad ze sztangą@3×8,Wyciskanie sztangi leżąc@3×8,Wiosłowanie sztangą@3×10,Wyciskanie nad głowę@2×10,Plank@2×45 s;Dzień 2:Martwy ciąg@3×5,Wyciskanie hantli na skosie@3×10,Podciąganie nachwytem@3×6–10,Wypychanie na suwnicy@3×10,Unoszenie nóg w zwisie@2×10"

        rawPlans.split("#").forEach { p ->
            val parts = p.split("=")
            if (parts.size >= 2) {
                val planName = parts[0]
                val days = mutableListOf<PlanDay>()
                parts[1].split(";").forEach { dayStr ->
                    val dayParts = dayStr.split(":")
                    if (dayParts.size >= 2) {
                        val dName = dayParts[0]
                        val exList = dayParts[1].split(",").map { exStr ->
                            val exParts = exStr.split("@")
                            PlanExercise(exParts[0], exParts.getOrElse(1) { "" })
                        }
                        days.add(PlanDay(dName, exList))
                    }
                }
                map[planName] = days
            }
        }
        map
    }

    private val EXL_RAW = mapOf(
        "Klatka" to "Wyciskanie sztangi leżąc,Wyciskanie sztangi na skosie,Wyciskanie sztangi na skosie ujemnym,Wyciskanie hantli leżąc,Wyciskanie hantli na skosie,Rozpiętki z hantlami,Rozpiętki na maszynie butterfly,Krzyżowanie linek wyciągu,Pompki,Pompki na podwyższeniu,Pompki diamentowe,Dipy na poręczach,Wyciskanie na maszynie,Pullover z hantlą",
        "Plecy" to "Martwy ciąg,Podciąganie nachwytem,Podciąganie podchwytem,Ściąganie drążka wyciągu górnego,Ściąganie wąskim uchwytem,Wiosłowanie sztangą,Wiosłowanie hantlą jedno-rącz,Wiosłowanie na wyciągu siedząc,Wiosłowanie T-bar,Wiosłowanie na maszynie,Face pull,Szrugsy ze sztangą,Hiperekstensje,Pullover na wyciągu,Good morning",
        "Nogi" to "Przysiad ze sztangą,Przysiad przedni,Przysiad bułgarski,Przysiad goblet,Przysiad hack,Wypychanie na suwnicy,Martwy ciąg rumuński,Martwy ciąg sumo,Wykroki z hantlami,Wykroki chodzone,Wejścia na podwyższenie,Prostowanie nóg na maszynie,Uginanie nóg leżąc,Uginanie nóg siedząc,Hip thrust,Wspięcia na palce stojąc,Wspięcia na palce siedząc,Odwodzenie nogi na wyciągu",
        "Barki" to "Wyciskanie nad głowę,Wyciskanie żołnierskie,Wyciskanie hantli siedząc,Wyciskanie Arnolda,Wyciskanie na maszynie barkowej,Unoszenie hantli bokiem,Unoszenie hantli w przód,Unoszenie hantli w opadzie,Unoszenie bokiem na wyciągu,Odwrotne rozpiętki na maszynie,Podciąganie sztangi wzdłuż tułowia,Szrugsy z hantlami",
        "Biceps" to "Uginanie ramion ze sztangą,Uginanie ramion ze sztangą łamaną,Uginanie ramion z hantlami,Uginanie młotkowe,Uginanie młotkowe z liną,Uginanie na modlitewniku,Uginanie skoncentrowane,Uginanie na wyciągu dolnym,Uginanie z hantlami na skosie,Uginanie Zottmana",
        "Triceps" to "Wyciskanie francuskie,Wyciskanie francuskie z hantlą,Wyciskanie wąskim chwytem,Prostowanie ramion na wyciągu,Prostowanie ramion z liną,Prostowanie ramion znad głowy,Prostowanie ramienia w opadzie,Prostowanie ramienia jednorącz na wyciągu,Dipy na ławce",
        "Brzuch i core" to "Plank,Plank boczny,Unoszenie nóg w zwisie,Unoszenie kolan w zwisie,Wznosy nóg leżąc,Spięcia brzucha,Allahy na wyciągu,Rosyjski skręt,Wspinaczka,Dead bug,Bird dog,Rowerek,Ab wheel,Skłony boczne z hantlą",
        "Cardio i całe ciało" to "Bieżnia,Marsz pod górę na bieżni,Rowerek stacjonarny,Interwały na rowerze,Orbitrek,Wioślarz,Skakanka,Burpees,Kettlebell swing,Thruster,Spacer farmera,Skoki na skrzynię"
    )

    private const val XR = "Klatka|Wyciskanie {a} {b}|sztangi,hantli,sztangi na suwnicy Smitha,kettlebell|leżąc,na skosie dodatnim,na skosie ujemnym;Klatka|Rozpiętki z {a} {b}|hantlami,linkami wyciągu,gumami oporowymi|leżąc,na skosie dodatnim,na skosie ujemnym;Klatka|Pompki {a}|klasyczne,szerokie,wąskie,diamentowe,z nogami na podwyższeniu,z rękami na podwyższeniu,z klaskaniem,z obciążeniem,z unoszeniem nogi,na piłce,na gumie oporowej,archer,spiderman,na pięściach;Klatka|Dipy {a}|na poręczach,na poręczach z pochyleniem tułowia,na poręczach z obciążeniem,na ławce,na maszynie,na pierścieniach,z gumą wspomagającą;Klatka|Wyciskanie na maszynie {a}|poziome,skośne,z góry,Hammer,jednorącz,z pauzą;Klatka|Krzyżowanie linek wyciągu {a}|górnych,dolnych,środkowych,jednorącz górnych,jednorącz dolnych,w wykroku,siedząc;Klatka|Pullover {a}|z hantlą leżąc,z hantlą w poprzek ławki,ze sztangą,z kettlebell,na wyciągu stojąc,na wyciągu klęcząc,na maszynie;Klatka|Butterfly {a}|na maszynie,jednorącz,z pauzą,na wyciągu;" +
            "Plecy|Podciąganie {a} {b}|nachwytem,podchwytem,neutralnym chwytem,szerokim chwytem,wąskim chwytem|na drążku,z obciążeniem,z gumą wspomagającą,z pauzą na górze;Plecy|Ściąganie drążka wyciągu górnego {a}|nachwytem,podchwytem,neutralnym chwytem,szerokim chwytem,wąskim chwytem,jednorącz,z pauzą,siedząc z oparciem,klęcząc;Plecy|Wiosłowanie sztangą {a}|nachwytem,podchwytem,w opadzie tułowia,z oparciem o ławkę,z pauzą,Pendlay,Yates,wąskim chwytem,szerokim chwytem,na suwnicy Smitha;Plecy|Wiosłowanie hantlą {a}|jednorącz w oparciu o ławkę,jednorącz w opadzie,obiema rękami w opadzie,leżąc na ławce skośnej,w podporze na ławce,z pauzą;Plecy|Wiosłowanie na wyciągu {a}|siedząc wąskim uchwytem,siedząc szerokim uchwytem,siedząc neutralnym uchwytem,siedząc jednorącz,stojąc w opadzie,stojąc jednorącz,z liną do klatki,z pauzą;Plecy|Wiosłowanie na maszynie {a}|z podparciem klatki,Hammer,siedząc,jednorącz,wysokie,niskie,T-bar,z pauzą;Plecy|Wiosłowanie {a}|z kettlebell,z gumą oporową,na TRX,na pierścieniach,australijskie,odwrócone pod stołem,gorylka;Plecy|Martwy ciąg {a}|klasyczny,sumo,rumuński,na prostych nogach,ze sztangą trap,z deficytu,z podwyższenia,z kettlebell,z hantlami,jednonóż,z gumami,z pauzą,na suwnicy Smitha,Zercher,rumuński jednonóż,rumuński z hantlami;Plecy|Szrugsy {a}|ze sztangą,z hantlami,na suwnicy Smitha,na maszynie,z kettlebell,ze sztangą z tyłu,z pauzą,na wyciągu;Plecy|Face pull {a}|z liną,stojąc,klęcząc,siedząc,z gumą oporową,z pauzą,jednorącz,w półklęku;Plecy|Prostowanie tułowia {a}|na ławce rzymskiej,na ławce rzymskiej z obciążeniem,na maszynie,z hantlą,z gumą,na piłce,w leżeniu przodem;Plecy|Unoszenie ramion w opadzie tułowia {a}|z hantlami,na wyciągu,z gumą,na maszynie,leżąc na skosie;Plecy|Good morning {a}|ze sztangą,z gumą,siedząc,na suwnicy Smitha;" +
            "Nogi|Przysiad {a}|ze sztangą,ze sztangą z przodu,na suwnicy Smitha,z hantlami,goblet z kettlebell,goblet z hantlą,z gumą,z pauzą,sumo,z wąskim rozstawem,z podwyższonymi piętami,na skrzyni,zatrzymany,dynamiczny,jednonóż,bułgarski z hantlami,bułgarski ze sztangą,hack na maszynie,na maszynie,Zercher,overhead,z wyskokiem;Nogi|Wykroki {a} {b}|w miejscu,chodzone,do tyłu,boczne,skośne,z podwyższenia|z hantlami,ze sztangą,z kettlebell,z gumą,z ciężarem własnym,na suwnicy Smitha;Nogi|Wypychanie na suwnicy {a}|skośnej,poziomej,pionowej,jednonóż,wąsko ułożonymi stopami,szeroko ułożonymi stopami,wysoko ułożonymi stopami,nisko ułożonymi stopami,z pauzą;Nogi|Prostowanie nóg {a}|na maszynie,na maszynie jednonóż,z pauzą,z gumą,z obciążeniem na kostkach,siedząc;Nogi|Uginanie nóg {a}|leżąc na maszynie,siedząc na maszynie,stojąc na maszynie,jednonóż,z pauzą,z hantlą,na piłce,z gumą,na wyciągu,nordic,na ławce skośnej;Nogi|Hip thrust {a}|ze sztangą,z hantlą,z kettlebell,na maszynie,jednonóż,z gumą na kolanach,z pauzą,na suwnicy Smitha,z deficytem,z ciężarem własnym;Nogi|Glute bridge {a}|z ciężarem własnym,jednonóż,z hantlą,z gumą,z pauzą,z uniesioną piętą,na piłce,z uniesieniem stóp;Nogi|Wspięcia na palce {a}|stojąc,siedząc,jednonóż,na suwnicy,na maszynie,ze sztangą,z hantlami,na stopniu,z pauzą,z gumą,na suwnicy Smitha,na suwnicy skośnej;Nogi|Wejścia na podwyższenie {a}|z hantlami,ze sztangą,z ciężarem własnym,bokiem,z kettlebell,z gumą,wysokie,niskie,z wyskokiem;Nogi|Odwodzenie nogi {a}|na wyciągu,z gumą,na maszynie,leżąc bokiem,stojąc z obciążeniem,w podporze klęcznym;Nogi|Przywodzenie nogi {a}|na wyciągu,na maszynie,z gumą,leżąc bokiem,w siadzie;Nogi|Pistolet {a}|z ciężarem własnym,z TRX,z wsparciem,z hantlą,na ławce,z kettlebell;Nogi|Skoki {a}|na skrzynię,z przysiadem,w dal,przez płotki,w miejscu,jednonóż,zeskoki,z wykroku,z obciążeniem,boczne,na stopień;" +
            "Barki|Wyciskanie {a} nad głowę {b}|sztangi,hantli,kettlebell,sztangi na suwnicy Smitha,hantli naprzemiennie,hantli neutralnym chwytem,hantli chwytem młotkowym|stojąc,siedząc,z pauzą;Barki|Unoszenie {a} {b}|hantli,linki wyciągu,gumy oporowej|bokiem,w przód,w opadzie,bokiem jednorącz,bokiem siedząc,w przód naprzemiennie;Barki|Wyciskanie Arnolda {a}|siedząc,stojąc,z pauzą,jednorącz,na skosie;Barki|Odwrotne rozpiętki {a}|na maszynie,z hantlami w opadzie,na wyciągu,z gumą,na ławce skośnej,jednorącz na wyciągu,z pauzą,z linkami skrzyżnymi;Barki|Podciąganie sztangi wzdłuż tułowia {a}|wąskim chwytem,szerokim chwytem,na suwnicy Smitha,z hantlami,ze sztangą łamaną,na wyciągu,z kettlebell,z pauzą;Barki|Wyciskanie żołnierskie {a}|ze sztangą,z hantlami,z kettlebell,z gumą,na suwnicy Smitha,push press,z pauzą;Barki|Wyciskanie na maszynie barkowej {a}|siedząc,jednorącz,z pauzą,neutralnym chwytem,Hammer;Barki|Rotacje zewnętrzne barku {a}|z hantlą leżąc na boku,na wyciągu,z gumą,stojąc,siedząc,z pauzą;Barki|Y-T-W {a}|na ławce skośnej,z hantlami,z gumą,na podłodze,na TRX;Barki|Unoszenie {a} w przód|sztangi,talerza,kettlebell,hantli neutralnym chwytem;" +
            "Biceps|Uginanie ramion z hantlami {a}|stojąc,siedząc,naprzemiennie,jednocześnie,na ławce skośnej,na ławce skośnej naprzemiennie,w oparciu o ławkę skośną,z supinacją,z pauzą,w podporze o udo;Biceps|Uginanie ramion ze sztangą {a}|prostą stojąc,łamaną stojąc,prostą wąskim chwytem,prostą szerokim chwytem,łamaną na modlitewniku,łamaną siedząc,prostą z pauzą,łamaną z pauzą,na suwnicy Smitha,21 powtórzeń;Biceps|Uginanie ramion na wyciągu {a}|dolnym z drążkiem,dolnym z liną,dolnym jednorącz,górnym obiema rękami,leżąc na plecach,stojąc tyłem,z pauzą;Biceps|Uginanie ramion z gumą {a}|stojąc,siedząc,jednorącz,naprzemiennie,z pauzą;Biceps|Uginanie młotkowe {a}|z hantlami stojąc,z hantlami siedząc,naprzemiennie,jednocześnie,z liną na wyciągu,z gumą,na ławce skośnej,w poprzek ciała,z pauzą;Biceps|Uginanie na modlitewniku {a}|ze sztangą łamaną,ze sztangą prostą,z hantlą jednorącz,na maszynie,na wyciągu,młotkowe,z pauzą;Biceps|Uginanie skoncentrowane {a}|z hantlą siedząc,na wyciągu,z gumą,z kettlebell,z pauzą;Biceps|Uginanie Zottmana {a}|z hantlami,siedząc,na skosie,na wyciągu,z pauzą;Biceps|Uginanie odwrotne {a}|ze sztangą,z hantlami,na wyciągu,z gumą,ze sztangą łamaną,z pauzą;" +
            "Triceps|Wyciskanie francuskie {a}|ze sztangą łamaną leżąc,ze sztangą prostą leżąc,z hantlami leżąc,z hantlą oburącz siedząc,z hantlą oburącz stojąc,jednorącz z hantlą,z liną na wyciągu,na skosie,na skosie ujemnym,na suwnicy Smitha,z gumą,z pauzą;Triceps|Prostowanie ramion {a}|na wyciągu z drążkiem,na wyciągu z liną,na wyciągu z drążkiem V,na wyciągu jednorącz nachwytem,na wyciągu jednorącz podchwytem,znad głowy z liną,znad głowy z hantlą,znad głowy z hantlą jednorącz,znad głowy z gumą,w opadzie z hantlą,w opadzie na wyciągu,z gumą,w podporze tyłem,z pauzą;Triceps|Wyciskanie wąskim chwytem {a}|leżąc,na skosie dodatnim,na suwnicy Smitha,z hantlami neutralnym chwytem,z pauzą,sztangi łamanej,w stojaku;Triceps|Dipy na ławce {a}|z ugiętymi nogami,z wyprostowanymi nogami,z obciążeniem,z nogami na podwyższeniu,jednonóż,z pauzą;Triceps|Pompki na triceps {a}|wąskie,diamentowe,na ławce,z łokciami przy ciele,na pochylni,z gumą,z obciążeniem,z asystą;Triceps|Kickback {a}|z hantlą,na wyciągu,z gumą,jednorącz na ławce,oburącz,z linką;Triceps|Triceps na maszynie {a}|siedząc,jednorącz,z pauzą,dip machine,stojąc;" +
            "Brzuch i core|Plank {a}|klasyczny,na łokciach,na wyprostowanych rękach,boczny,boczny z uniesieniem nogi,z unoszeniem nogi,z unoszeniem ręki,z dotykaniem barku,na piłce,na TRX,z obciążeniem,odwrócony,dynamiczny,z wiosłowaniem,z rotacją,z podnoszeniem kolana;Brzuch i core|Unoszenie nóg {a}|w zwisie,w zwisie ze zgiętymi kolanami,leżąc,leżąc z obciążeniem,na poręczach,na ławce skośnej,w zwisie bokiem,na ławce rzymskiej,z piłką między stopami,na maszynie;Brzuch i core|Spięcia brzucha {a}|leżąc,na ławce skośnej,na wyciągu klęcząc,na maszynie,z obciążeniem,na piłce,z pauzą,odwrócone,bokiem,z rotacją;Brzuch i core|Rosyjski skręt {a}|z ciężarem,z piłką lekarską,z talerzem,z hantlą,bez ciężaru,na ławce,z uniesionymi stopami,z kettlebell,na wyciągu,z gumą;Brzuch i core|Allahy {a}|na wyciągu klęcząc,na wyciągu stojąc,z liną,z gumą,z pauzą,bokiem;Brzuch i core|Wyciskanie Pallofa {a}|na wyciągu stojąc,z gumą,klęcząc,w wykroku,z krokiem bokiem,z pauzą;Brzuch i core|Dead bug {a}|klasyczny,z ciężarem,z gumą,z piłką,z pauzą,z oddechem,z kettlebell,jednostronny;Brzuch i core|Bird dog {a}|klasyczny,z gumą,z pauzą,z ciężarem,na piłce,z łokciem do kolana,z obciążeniem na plecach;Brzuch i core|Rowerek {a}|leżąc,w zwisie,z obciążeniem,na ławce,wolny,z pauzą,dynamiczny;Brzuch i core|Ab wheel {a}|z kolan,na stojąco,z pauzą,na skos,z TRX,z gumą;Brzuch i core|Wspinaczka {a}|klasyczna,krzyżowa,boczna,na sliderach,wolna,z piłką,z TRX,w zwisie;Brzuch i core|Skłony boczne {a}|z hantlą,z kettlebell,na wyciągu,na ławce rzymskiej,z gumą,ze sztangą,na maszynie,z pauzą;Brzuch i core|Hollow body {a}|klasyczny,z kołysaniem,z gumą,z piłką,z obciążeniem,z uniesionymi rękami;" +
            "Cardio i całe ciało|Bieg {a}|na bieżni,w terenie,interwałowy,tempowy,długi spokojny,sprinty 100 m,sprinty pod górę,po schodach,w miejscu,z wysokim unoszeniem kolan,w pochyleniu;Cardio i całe ciało|Marsz {a}|na bieżni,pod górę na bieżni,w terenie,z obciążeniem,z plecakiem,nordic walking,po schodach,szybki,z hantlami;Cardio i całe ciało|Rowerek stacjonarny {a}|spokojny,interwały 30/30,interwały HIIT,tempo,pod górę,sprinty,z oporem,stojąc,regeneracyjny;Cardio i całe ciało|Wioślarz {a}|spokojny,interwały,sprinty 500 m,na dystans 2000 m,tempo,z oporem;Cardio i całe ciało|Orbitrek {a}|spokojny,interwały,pod górę,do tyłu,tempo;Cardio i całe ciało|Kettlebell {a}|swing oburącz,swing jednorącz,swing rosyjski,swing amerykański,martwy ciąg sumo,snatch,clean,clean and press,turkish get-up,przysiad goblet,halo,windmill;Cardio i całe ciało|Burpees {a}|klasyczne,z pompką,z wyskokiem,z przeskokiem,bez pompki,z hantlami,z podciąganiem,bokiem,jednonóż,z klaskaniem;Cardio i całe ciało|Skakanka {a}|podstawowa,podwójna,naprzemienna,jednonóż,boxer,z krzyżowaniem,z wysokim kolanem,szybka;Cardio i całe ciało|Thruster {a}|ze sztangą,z hantlami,z kettlebell,z piłką lekarską,z gumą,jednorącz,z pauzą;Cardio i całe ciało|Rzuty piłką lekarską {a}|w ścianę,nad głową,w podłogę,bokiem,z przysiadu,rotacyjne,z klatki,z wyskokiem;Cardio i całe ciało|Battle ropes {a}|fale naprzemienne,fale jednoczesne,uderzenia,okręgi,rozpiętki,z przysiadu,jednorącz,z wykrokiem;Cardio i całe ciało|Pływanie {a}|kraulem,żabką,grzbietem,motylkiem,interwały,z deską,z kolankiem;" +
            "Mobilność i rozciąganie|Rozciąganie {a}|mięśni czworogłowych stojąc,mięśni czworogłowych leżąc bokiem,mięśni dwugłowych siedząc,mięśni dwugłowych stojąc,pośladków leżąc,pośladków w siadzie,łydek przy ścianie,łydek na stopniu,zginaczy bioder w wykroku,przywodzicieli w siadzie motylkowym,przywodzicieli w szerokim rozkroku,klatki piersiowej przy framudze,pleców w pozycji dziecka,barków z ramieniem w poprzek,tricepsu za głową,bicepsu przy ścianie,przedramion,szyi,dolnej części pleców,pasma biodrowo-piszczelowego;Mobilność i rozciąganie|Mobilność {a}|bioder kółka,bioder 90/90,kręgosłupa piersiowego,kręgosłupa kot-krowa,barków kółka,barków z kijem,kostek,nadgarstków,szyi,głęboki przysiad,łopatek,wykrok z rotacją tułowia;Mobilność i rozciąganie|Joga {a}|pies z głową w dół,kobra,dziecko,wojownik I,wojownik II,trójkąt,drzewo,gołąb,kot i krowa,most"

    val EXERCISE_LIBRARY: Map<String, List<String>> by lazy {
        val seen = mutableSetOf<String>()
        val result = mutableMapOf<String, MutableList<String>>()

        EXL_RAW.forEach { (cat, itemsStr) ->
            val items = itemsStr.split(",")
            val list = mutableListOf<String>()
            items.forEach { name ->
                val trimmed = name.trim()
                if (trimmed.isNotEmpty() && seen.add(trimmed)) {
                    list.add(trimmed)
                }
            }
            result[cat] = list
        }

        XR.split(";").forEach { row ->
            val p = row.split("|")
            if (p.size >= 3) {
                val group = p[0]
                val template = p[1]
                val aList = p[2].split(",")
                val bList = if (p.size > 3) p[3].split(",") else listOf("")
                val list = result.getOrPut(group) { mutableListOf() }
                aList.forEach { a ->
                    bList.forEach { b ->
                        val n = template.replace("{a}", a).replace("{b}", b).replace(Regex("\\s+"), " ").trim()
                        if (n.isNotEmpty() && seen.add(n)) {
                            list.add(n)
                        }
                    }
                }
            }
        }
        result
    }

    val TOTAL_LIBRARY_EXERCISES: Int by lazy {
        EXERCISE_LIBRARY.values.sumOf { it.size }
    }
}
