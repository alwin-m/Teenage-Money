package com.teenagemoney.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TeenageMoneyDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "teenagemoney.db"
        // Version 2 adds transaction timestamps used for correct activity grouping.
        // Upgrades must preserve the user's locally stored money and transactions.
        const val DATABASE_VERSION = 2

        private const val TABLE_PREFERENCES = "preferences"
        private const val TABLE_SPACES = "spaces"
        private const val TABLE_TRANSACTIONS = "transactions"
        private const val TABLE_GOALS = "goals"

        @Volatile
        private var instance: TeenageMoneyDbHelper? = null

        fun getInstance(context: Context): TeenageMoneyDbHelper {
            return instance ?: synchronized(this) {
                instance ?: TeenageMoneyDbHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_PREFERENCES (
                key TEXT PRIMARY KEY,
                value TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_SPACES (
                name TEXT PRIMARY KEY,
                note TEXT,
                balance REAL,
                tint TEXT,
                icon TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_TRANSACTIONS (
                id INTEGER PRIMARY KEY,
                type TEXT,
                title TEXT,
                category TEXT,
                source TEXT,
                amount REAL,
                day TEXT,
                icon TEXT,
                color TEXT,
                timestamp TEXT,
                receipt_number TEXT,
                remaining REAL,
                epoch_ms INTEGER DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_GOALS (
                id INTEGER PRIMARY KEY,
                title TEXT,
                target REAL,
                saved REAL,
                tint TEXT,
                emoji TEXT
            )
            """.trimIndent()
        )

        // Seed EMPTY spaces — real balances start at 0, user adds money themselves
        val initialSpaces = listOf(
            MoneySpace("Spending", "Everyday money", 0.0, "peach", "↗"),
            MoneySpace("Savings",  "Future you",     0.0, "mint",   "✧"),
            MoneySpace("Emergency","Just in case",   0.0, "lavender","＋")
        )
        for (s in initialSpaces) {
            val cv = ContentValues().apply {
                put("name",    s.name)
                put("note",    s.note)
                put("balance", s.balance)
                put("tint",    s.tint)
                put("icon",    s.icon)
            }
            db.insert(TABLE_SPACES, null, cv)
        }
        // No seed transactions, no seed goals — clean slate for the user
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val columns = mutableSetOf<String>()
            db.rawQuery("PRAGMA table_info($TABLE_TRANSACTIONS)", null).use { cursor ->
                while (cursor.moveToNext()) columns += cursor.getString(1)
            }
            if ("epoch_ms" !in columns) {
                db.execSQL("ALTER TABLE $TABLE_TRANSACTIONS ADD COLUMN epoch_ms INTEGER DEFAULT 0")
            }
        }
    }

    // --- Profile & Preferences ---

    fun getPreference(key: String, defaultValue: String = ""): String {
        val db = readableDatabase
        db.rawQuery("SELECT value FROM $TABLE_PREFERENCES WHERE key = ?", arrayOf(key)).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(0) ?: defaultValue
            }
        }
        return defaultValue
    }

    fun setPreference(key: String, value: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("key",   key)
            put("value", value)
        }
        db.insertWithOnConflict(TABLE_PREFERENCES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getUserProfile(): UserProfile? {
        val completed = getPreference("onboarding_completed", "false") == "true"
        val name = getPreference("name", "")
        if (name.isEmpty() && !completed) {
            return null
        }
        val dob    = getPreference("dob",    "")
        val age    = getPreference("age",    "0").toIntOrNull() ?: 0
        val status = getPreference("status", "")
        return UserProfile(
            name = name,
            dateOfBirth = dob,
            age = age,
            status = status,
            onboardingCompleted = completed
        )
    }

    fun saveUserProfile(profile: UserProfile) {
        setPreference("name",                  profile.name)
        setPreference("dob",                   profile.dateOfBirth)
        setPreference("age",                   profile.age.toString())
        setPreference("status",                profile.status)
        setPreference("onboarding_completed",  if (profile.onboardingCompleted) "true" else "false")
    }

    // --- Spaces ---

    fun getSpaces(): List<MoneySpace> {
        val list = mutableListOf<MoneySpace>()
        val db = readableDatabase
        db.rawQuery("SELECT name, note, balance, tint, icon FROM $TABLE_SPACES ORDER BY rowid ASC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    MoneySpace(
                        name    = cursor.getString(0),
                        note    = cursor.getString(1) ?: "",
                        balance = cursor.getDouble(2),
                        tint    = cursor.getString(3) ?: "peach",
                        icon    = cursor.getString(4) ?: "↗"
                    )
                )
            }
        }
        return list
    }

    fun updateSpaceBalance(name: String, newBalance: Double) {
        val db = writableDatabase
        val cv = ContentValues().apply { put("balance", newBalance) }
        db.update(TABLE_SPACES, cv, "name = ?", arrayOf(name))
    }

    fun addSpace(space: MoneySpace): Boolean {
        val cv = ContentValues().apply {
            put("name", space.name)
            put("note", space.note)
            put("balance", space.balance)
            put("tint", space.tint)
            put("icon", space.icon)
        }
        return writableDatabase.insert(TABLE_SPACES, null, cv) != -1L
    }

    // --- Transactions ---

    fun getTransactions(): List<TransactionItem> {
        val list = mutableListOf<TransactionItem>()
        val db = readableDatabase
        db.rawQuery(
            "SELECT id, type, title, category, source, amount, day, icon, color, timestamp, receipt_number, remaining, epoch_ms FROM $TABLE_TRANSACTIONS ORDER BY epoch_ms DESC, id DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    TransactionItem(
                        id            = cursor.getLong(0),
                        type          = cursor.getString(1) ?: "expense",
                        title         = cursor.getString(2) ?: "",
                        category      = cursor.getString(3) ?: "",
                        source        = cursor.getString(4) ?: "UPI",
                        amount        = cursor.getDouble(5),
                        day           = cursor.getString(6) ?: "Today",
                        icon          = cursor.getString(7) ?: "✦",
                        color         = cursor.getString(8) ?: "orange",
                        timestamp     = cursor.getString(9) ?: "",
                        receiptNumber = cursor.getString(10) ?: "",
                        remaining     = cursor.getDouble(11),
                        epochMs       = cursor.getLong(12)
                    )
                )
            }
        }
        return list
    }

    fun addTransaction(t: TransactionItem) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("id",             t.id)
            put("type",           t.type)
            put("title",          t.title)
            put("category",       t.category)
            put("source",         t.source)
            put("amount",         t.amount)
            put("day",            t.day)
            put("icon",           t.icon)
            put("color",          t.color)
            put("timestamp",      t.timestamp)
            put("receipt_number", t.receiptNumber)
            put("remaining",      t.remaining)
            put("epoch_ms",       t.epochMs)
        }
        db.insertWithOnConflict(TABLE_TRANSACTIONS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // --- Goals ---

    fun getGoals(): List<SavingsGoal> {
        val list = mutableListOf<SavingsGoal>()
        val db = readableDatabase
        db.rawQuery("SELECT id, title, target, saved, tint, emoji FROM $TABLE_GOALS ORDER BY id ASC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    SavingsGoal(
                        id    = cursor.getLong(0),
                        title = cursor.getString(1) ?: "",
                        target= cursor.getDouble(2),
                        saved = cursor.getDouble(3),
                        tint  = cursor.getString(4) ?: "peach",
                        emoji = cursor.getString(5) ?: "★"
                    )
                )
            }
        }
        return list
    }

}
