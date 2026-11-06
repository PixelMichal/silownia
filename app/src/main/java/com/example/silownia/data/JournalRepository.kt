package com.example.silownia.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class JournalRepository(context: Context) {
    private val prefs = context.getSharedPreferences("dziennik_prefs", Context.MODE_PRIVATE)
    private val STORAGE_KEY = "dziennik-v1"

    fun loadState(): JournalState {
        val jsonStr = prefs.getString(STORAGE_KEY, null) ?: return JournalState()
        return try {
            val json = JSONObject(jsonStr)

            val goalsObj = json.optJSONObject("goals")
            val goals = if (goalsObj != null) {
                Goals(
                    k = goalsObj.optInt("k", 2200),
                    p = goalsObj.optInt("p", 150)
                )
            } else Goals()

            val mealsList = mutableListOf<MealEntry>()
            val mealsArr = json.optJSONArray("meals")
            if (mealsArr != null) {
                for (i in 0 until mealsArr.length()) {
                    val m = mealsArr.getJSONObject(i)
                    mealsList.add(
                        MealEntry(
                            id = m.optLong("id", System.currentTimeMillis()),
                            d = m.optString("d", ""),
                            t = m.optString("t").takeIf { it.isNotEmpty() },
                            n = m.optString("n", ""),
                            k = m.optDouble("k", 0.0),
                            p = m.optDouble("p", 0.0),
                            c = m.optDouble("c", 0.0),
                            f = m.optDouble("f", 0.0)
                        )
                    )
                }
            }

            val setsList = mutableListOf<WorkoutSetEntry>()
            val setsArr = json.optJSONArray("sets")
            if (setsArr != null) {
                for (i in 0 until setsArr.length()) {
                    val s = setsArr.getJSONObject(i)
                    setsList.add(
                        WorkoutSetEntry(
                            id = s.optLong("id", System.currentTimeMillis()),
                            d = s.optString("d", ""),
                            e = s.optString("e", ""),
                            kg = s.optDouble("kg", 0.0),
                            r = s.optInt("r", 0),
                            s = s.optInt("s", 1)
                        )
                    )
                }
            }

            val weightsList = mutableListOf<WeightEntry>()
            val weightsArr = json.optJSONArray("weights")
            if (weightsArr != null) {
                for (i in 0 until weightsArr.length()) {
                    val w = weightsArr.getJSONObject(i)
                    weightsList.add(
                        WeightEntry(
                            d = w.optString("d", ""),
                            kg = w.optDouble("kg", 0.0)
                        )
                    )
                }
            }

            val bioObj = json.optJSONObject("bio")
            val bio = if (bioObj != null) {
                BioData(
                    w = bioObj.optDouble("w", 0.0),
                    h = bioObj.optDouble("h", 0.0),
                    a = bioObj.optInt("a", 0),
                    x = bioObj.optString("x", "m"),
                    v = bioObj.optString("v", "1.55"),
                    g = bioObj.optString("g", "utr")
                )
            } else null

            val suppsList = mutableListOf<SuppItem>()
            val suppsArr = json.optJSONArray("supps")
            if (suppsArr != null) {
                for (i in 0 until suppsArr.length()) {
                    val s = suppsArr.getJSONObject(i)
                    suppsList.add(
                        SuppItem(
                            id = s.optLong("id", System.currentTimeMillis()),
                            n = s.optString("n", ""),
                            dose = s.optString("dose", "")
                        )
                    )
                }
            }

            val slMap = mutableMapOf<String, List<Long>>()
            val slObj = json.optJSONObject("sl")
            if (slObj != null) {
                val keys = slObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    val arr = slObj.optJSONArray(k)
                    if (arr != null) {
                        val ids = mutableListOf<Long>()
                        for (i in 0 until arr.length()) {
                            ids.add(arr.getLong(i))
                        }
                        slMap[k] = ids
                    }
                }
            }

            val sleepList = mutableListOf<SleepEntry>()
            val sleepArr = json.optJSONArray("sleep")
            if (sleepArr != null) {
                for (i in 0 until sleepArr.length()) {
                    val se = sleepArr.getJSONObject(i)
                    sleepList.add(
                        SleepEntry(
                            d = se.optString("d", ""),
                            b = se.optString("b", "23:00"),
                            w = se.optString("w", "07:00"),
                            q = se.optInt("q", 3),
                            dp = if (se.has("dp") && !se.isNull("dp")) se.getInt("dp") else null,
                            lt = if (se.has("lt") && !se.isNull("lt")) se.getInt("lt") else null
                        )
                    )
                }
            }

            val sg = if (json.has("sg") && !json.isNull("sg")) json.optDouble("sg", 8.0) else 8.0

            val myrecList = mutableListOf<Recipe>()
            val myrecArr = json.optJSONArray("myrec")
            if (myrecArr != null) {
                for (i in 0 until myrecArr.length()) {
                    val r = myrecArr.getJSONObject(i)
                    myrecList.add(
                        Recipe(
                            id = if (r.has("id")) r.getLong("id") else null,
                            cat = r.optString("cat").takeIf { it.isNotEmpty() },
                            n = r.optString("n", ""),
                            k = r.optInt("k", 0),
                            p = r.optInt("p", 0),
                            c = r.optInt("c", 0),
                            f = r.optInt("f", 0),
                            i = r.optString("i", ""),
                            s = r.optString("s", "")
                        )
                    )
                }
            }

            val myexList = mutableListOf<CustomExercise>()
            val myexArr = json.optJSONArray("myex")
            if (myexArr != null) {
                for (i in 0 until myexArr.length()) {
                    val e = myexArr.getJSONObject(i)
                    myexList.add(
                        CustomExercise(
                            id = e.optLong("id", System.currentTimeMillis()),
                            n = e.optString("n", ""),
                            sr = e.optString("sr", ""),
                            day = e.optString("day", "")
                        )
                    )
                }
            }

            val stepsMap = mutableMapOf<String, Int>()
            val stepsObj = json.optJSONObject("steps")
            if (stepsObj != null) {
                val keys = stepsObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    stepsMap[k] = stepsObj.optInt(k, 0)
                }
            }

            val stgObj = json.optJSONObject("stg")
            val stg = if (stgObj != null) {
                StepGoalConfig(
                    v = stgObj.optInt("v", 8000),
                    g = stgObj.optString("g").takeIf { it.isNotEmpty() }
                )
            } else null

            JournalState(
                goals = goals,
                meals = mealsList,
                sets = setsList,
                weights = weightsList,
                bio = bio,
                supps = suppsList,
                sl = slMap,
                sleep = sleepList,
                sg = sg,
                myrec = myrecList,
                myex = myexList,
                steps = stepsMap,
                stg = stg
            )
        } catch (_: Exception) {
            JournalState()
        }
    }

    fun saveState(state: JournalState) {
        try {
            val json = JSONObject()

            val goalsObj = JSONObject()
            goalsObj.put("k", state.goals.k)
            goalsObj.put("p", state.goals.p)
            json.put("goals", goalsObj)

            val mealsArr = JSONArray()
            state.meals.forEach { m ->
                val mObj = JSONObject()
                mObj.put("id", m.id)
                mObj.put("d", m.d)
                m.t?.let { mObj.put("t", it) }
                mObj.put("n", m.n)
                mObj.put("k", m.k)
                mObj.put("p", m.p)
                mObj.put("c", m.c)
                mObj.put("f", m.f)
                mealsArr.put(mObj)
            }
            json.put("meals", mealsArr)

            val setsArr = JSONArray()
            state.sets.forEach { s ->
                val sObj = JSONObject()
                sObj.put("id", s.id)
                sObj.put("d", s.d)
                sObj.put("e", s.e)
                sObj.put("kg", s.kg)
                sObj.put("r", s.r)
                sObj.put("s", s.s)
                setsArr.put(sObj)
            }
            json.put("sets", setsArr)

            val weightsArr = JSONArray()
            state.weights.forEach { w ->
                val wObj = JSONObject()
                wObj.put("d", w.d)
                wObj.put("kg", w.kg)
                weightsArr.put(wObj)
            }
            json.put("weights", weightsArr)

            state.bio?.let { b ->
                val bObj = JSONObject()
                bObj.put("w", b.w)
                bObj.put("h", b.h)
                bObj.put("a", b.a)
                bObj.put("x", b.x)
                bObj.put("v", b.v)
                bObj.put("g", b.g)
                json.put("bio", bObj)
            }

            val suppsArr = JSONArray()
            state.supps.forEach { s ->
                val sObj = JSONObject()
                sObj.put("id", s.id)
                sObj.put("n", s.n)
                sObj.put("dose", s.dose)
                suppsArr.put(sObj)
            }
            json.put("supps", suppsArr)

            val slObj = JSONObject()
            state.sl.forEach { (k, v) ->
                val arr = JSONArray()
                v.forEach { arr.put(it) }
                slObj.put(k, arr)
            }
            json.put("sl", slObj)

            val sleepArr = JSONArray()
            state.sleep.forEach { se ->
                val sObj = JSONObject()
                sObj.put("d", se.d)
                sObj.put("b", se.b)
                sObj.put("w", se.w)
                sObj.put("q", se.q)
                se.dp?.let { sObj.put("dp", it) }
                se.lt?.let { sObj.put("lt", it) }
                sleepArr.put(sObj)
            }
            json.put("sleep", sleepArr)

            state.sg?.let { json.put("sg", it) }

            val myrecArr = JSONArray()
            state.myrec.forEach { r ->
                val rObj = JSONObject()
                r.id?.let { rObj.put("id", it) }
                r.cat?.let { rObj.put("cat", it) }
                rObj.put("n", r.n)
                rObj.put("k", r.k)
                rObj.put("p", r.p)
                rObj.put("c", r.c)
                rObj.put("f", r.f)
                rObj.put("i", r.i)
                rObj.put("s", r.s)
                myrecArr.put(rObj)
            }
            json.put("myrec", myrecArr)

            val myexArr = JSONArray()
            state.myex.forEach { e ->
                val eObj = JSONObject()
                eObj.put("id", e.id)
                eObj.put("n", e.n)
                eObj.put("sr", e.sr)
                eObj.put("day", e.day)
                myexArr.put(eObj)
            }
            json.put("myex", myexArr)

            val stepsObj = JSONObject()
            state.steps.forEach { (k, v) ->
                stepsObj.put(k, v)
            }
            json.put("steps", stepsObj)

            state.stg?.let { stg ->
                val stgObj = JSONObject()
                stgObj.put("v", stg.v)
                stg.g?.let { stgObj.put("g", it) }
                json.put("stg", stgObj)
            }

            prefs.edit().putString(STORAGE_KEY, json.toString()).apply()
        } catch (_: Exception) {
            // Ignore persistence errors
        }
    }
}
