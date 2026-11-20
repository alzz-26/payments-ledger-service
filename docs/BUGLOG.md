# Bug log

Bugs and setup problems that cost real time. Newest at the bottom.

| # | Date | M | Symptom | Root cause | Fix | Time lost | Lesson |
|---|---|---|---|---|---|---|---|
| 1 | 2026-11-20 | M0 | App failed at startup: `FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"` | The JDBC driver sends the JVM timezone to Postgres; on Windows in India Java reports the legacy ID `Asia/Calcutta`, which the `postgres:16` container rejects | Run the JVM in UTC: `-Duser.timezone=UTC` in `pom.xml` (`<argLine>` for tests, `<jvmArguments>` for `spring-boot:run`) | ~__ min | Read the **last** `Caused by:` first. Keep all time in UTC (see DECISIONS #2) |
| 2 | 2026-11-20 | M0 | Editor flagged the pom; the app wouldn't have started | Added a **second** `spring-boot-maven-plugin` block, and split the flag (`Duser.timezone` in one place, `UTC` in the other) | One plugin block containing `<configuration>`; the full flag `-Duser.timezone=UTC` in both places | ~__ min | Edit the existing part, don't paste a copy. Without the leading `-`, the JVM takes the text as a class name |
| 3 | 2026-11-20 | M0 | First migration silently ignored, no error | Named it `V1_init.sql`; Flyway needs **two** underscores: `V1__init.sql` | Renamed it | ~__ min | Silent failures are the worst. Check that something happened (`flyway_schema_history`), not only that nothing crashed |
| 4 | 2026-11-20 | M0 | `PingController` didn't compile: `cannot find symbol: RequestMapping` and `Entry cannot be converted to Map` | Imported `PathVariable` instead of `RequestMapping`; used `Map.entry` (one pair) where `Map.of` (a map) was needed. Also built before saving | Correct import, `Map.of(...)`, save before building | ~__ min | Check imports first; the return type is a promise the compiler checks; the build reads the file on disk |
