# Concepts

What I learned while building this project, one milestone at a time. Rows marked ✍️ are things I found myself.

## M0: Skeleton

### Spring Boot basics
| Concept | In one line | Why it matters / gotcha |
|---|---|---|
| Layers | Controller (HTTP in and out) → Service (business rules, transactions) → Repository (database) | The service is not a wrapper around the repository. It holds the rules and the `@Transactional` boundary that spans several repositories |
| Dependency injection | Spring creates objects (beans) and passes them in through the constructor; you never write `new` | Easy to swap in fakes for tests; loose coupling |
| Beans are singletons | One instance is created at startup by **component scanning** (the main class's package and its subpackages) and shared by every request | Tomcat calls it from many threads, so **never keep request data in fields**. A class outside the scanned package isn't found, which shows up as a 404 |
| `@RestController` | `@Controller` + `@ResponseBody`: return values become the response body | Jackson turns objects, maps and records into JSON automatically |
| `Map.of(...)` | Immutable map factory (Java 9+), up to 10 pairs, no nulls | Immutable means thread-safe and can't change by accident. `Map.entry` creates a single pair, not a map. Use records for real response types |
| Embedded Tomcat | The web server is a library inside the jar; `java -jar app.jar` runs everything | Jar = server inside the app; War = deploy into a separately installed Tomcat (the old way) |
| `javax` → `jakarta` | Java EE moved to the Eclipse Foundation and its packages were renamed (Jakarta EE 9) | Tomcat 9 = `javax`, Tomcat 10.1+ = `jakarta`. Mixing them gives class-not-found errors. Boot 3+ is `jakarta` only |
| Starter parent | `spring-boot-starter-parent` picks compatible versions for every library | A hand-written pom with hand-picked versions drifts apart and breaks |
| `mvnw` wrapper | A script that downloads the right Maven version automatically | No Maven install needed; everyone (and CI) builds with the same version |
| Properties vs YAML | Two formats for the same config | Properties is flat and safe; in YAML, indentation matters |
| Java LTS | Long-term support releases: 17, 21, 25 | A JDK only compiles for its own version or older (JDK 22 can build 21, not 25) |

### Database
| Concept | In one line | Why it matters / gotcha |
|---|---|---|
| Flyway | Versioned SQL migrations (`V1__init.sql`), run at startup and recorded in `flyway_schema_history` with a checksum | Name it with **two underscores**, otherwise the file is silently skipped. **Never edit a migration that has run**; add a new one |
| `ddl-auto=validate` | Hibernate only checks that entities match the tables | `update` changes tables silently, with no history and no safe renames or drops. Flyway owns the schema |
| Postgres in Docker Compose | `docker compose up -d` starts the same database for everyone | A named volume keeps data across restarts; nothing is installed on the PC |

### Running and debugging
| Concept | In one line | Why it matters / gotcha |
|---|---|---|
| Ping vs health | `/api/ping` = the app is alive; `/actuator/health` = the app **and** its dependencies (DB) are up, otherwise 503 | Load balancers and Kubernetes use liveness/readiness probes. A dead app's "error" is no response at all |
| Default errors | Spring returns 404 (wrong path), 405 (wrong method) and 500 (exception) as JSON | Custom errors come later with `@RestControllerAdvice` + ProblemDetail (RFC 9457) |
| Stack traces | Read the **last** `Caused by:` first | Everything above it is a chain of consequences |
| JVM flags | `-D` + property name + `=value`, e.g. `-Duser.timezone=UTC` | Without the leading `-`, the JVM treats the text as the main class name |
| UTC everywhere | Store and process time in UTC; convert only for display | One timeline on every server, no daylight-saving gaps. Also fixed the `Asia/Calcutta` connection error |
| CLI vs editor | The `./mvnw` build is the source of truth | If the CLI builds but the editor complains, reload the project or clean the editor workspace |
| Save before building | The build reads the file on disk, not what's open in the editor | A classic "but I fixed it!" moment |
| Conventional Commits | `type: imperative summary` with types `feat`, `fix`, `docs`, `chore`, `test`, `refactor` | Small, focused commits make history and reviews easy to read |

## M1a: Accounts and the ledger schema

| Concept | In one line | Why it matters / gotcha |
|---|---|---|
| Never `UPDATE balance` | Store every money movement as an immutable row; **balance = SUM(entries)** | Gives a full audit trail. Mistakes are fixed with a new correcting entry, never by editing an old one |
| Double entry | Every transaction has ≥2 entries that **sum to zero**; money moves *from* one account *to* another | A sum other than zero means a bug, and that can be checked automatically. Deposits come from a `SYSTEM` account (the outside world) |
| Debit / credit | The accountants' names for the two sides | Here represented as signed amounts (− / +) |
| No `double` for money | `0.1 + 0.2 = 0.30000000000000004` | Use minor units (`long` paise, `BIGINT`) or `BigDecimal` (`NUMERIC`). Minor units are simpler; you need to know each currency's decimals (JPY 0, KWD 3) |
| JPA entity rules | `@Entity`, `@Id`, a no-arg constructor (can be `protected`), not `final` | JPA builds and proxies entities itself, so **records can't be entities** |
| `@GeneratedValue(IDENTITY)` | The database generates the id (`GENERATED ALWAYS AS IDENTITY`) | The id only exists after the insert |
| `@Enumerated(EnumType.STRING)` | Stores the enum's name, e.g. `'USER'` | `ORDINAL` stores 0, 1, 2…, so reordering or inserting an enum value silently corrupts existing data |
| `@ManyToOne(fetch = LAZY)` | Each entry belongs to one account and one transaction; they're loaded only when used | Eager loading everywhere leads to slow, huge queries |
| `Instant` ↔ `TIMESTAMPTZ` | A UTC point in time in both Java and Postgres | Follows from the UTC decision |
| DB constraints | `UNIQUE(owner, currency)`, foreign keys, `CHECK (amount <> 0)`, `NOT NULL` | The database is the last line of defence; constraints catch bugs that application code misses |
| Index | Speeds up lookups such as `WHERE account_id = ?` | Balances are queried per account all the time |
| Package by feature | Everything for one feature (`ledger/`) lives in one package, not split into `entities/`, `repositories/`… | One place to understand a feature; add subfolders only past ~10–15 files |
| Domain concept vs role | **Concept** = a real-world business thing that exists without software (`Account`, `LedgerEntry`, `Transfer`), named after the thing. **Role** = a job a class does in the software for a concept, named thing + job suffix (`AccountRepository` stores it, `AccountController` handles HTTP, `LedgerService` holds the business logic, `DepositRequest` / `AccountResponse` are the API input and output shapes) | No `AccountEntity`: don't name the core concept after its storage mechanism. Test: would an accountant use the word? Analogy: a hospital *patient* (concept) vs *receptionist, nurse, billing clerk* (roles) |
| Entity ≠ API response | `Account` mirrors the DB table; `AccountResponse` is the contract with API clients | Returning entities directly means a DB change breaks the API and can leak internal fields. Keeping them separate lets each change independently |

### Learned while building M1a
| Concept | In one line | Why it matters / gotcha |
|---|---|---|
| Spring Data repositories | `interface AccountRepository extends JpaRepository<Account, Long>`; Spring writes the implementation at startup | You get `save`, `findById`, `findAll`… for free. It must be an **interface**; `@Repository` is optional |
| Relations vs raw ids | `@ManyToOne Account account` + `@JoinColumn(name = "account_id")`, not `Long accountId` | Type safety: with three `Long` parameters in a row, a swap compiles and money goes to the wrong account; with real types it doesn't compile. With `LAZY`, `getAccount().getId()` doesn't run a query |
| camelCase ↔ snake_case | Spring Boot maps `transactionType` to the `transaction_type` column automatically | `@Column(name = …)` is only needed when the names really differ |
| NULL passes CHECK | `CHECK (amount != 0)` with NULL is "unknown", which counts as a pass | Always pair a CHECK with `NOT NULL` |
| Transactional DDL | Postgres runs each migration inside a transaction | A failing migration rolls back completely: fix the file and rerun. MySQL and Oracle can't do this |
| Hibernate `@Check` | Only used when **Hibernate** generates the tables | With Flyway it does nothing. Constraints belong in the SQL migration |
| `ddl-auto=validate` | At startup, Hibernate compares entities with the real tables and refuses to start on any mismatch | Catches Java/SQL drift immediately (e.g. `missing column [type]`) |
| The never-edit-migrations rule | It protects **shared** history: teammates, CI, production. Flyway checksums include comments | Unshared local work: reset with `docker compose down -v` (deletes all local data!) and edit. Shared: add a new migration, e.g. `ALTER TABLE … RENAME COLUMN …` |
| Connection refused vs rejected | "Refused" = nothing is listening on the port (the DB isn't running); "FATAL: …" = the DB answered and said no | Different symptom, different cause: check the container first vs check the config |

**Questions I should be able to answer:**
1. Why `EnumType.STRING` and not `ORDINAL`?
2. Why is there no `balance` column on `accounts`?
3. What should happen if someone tries to `UPDATE` or `DELETE` a ledger entry?

## Habits
Small working habits that keep preventing the bugs in BUGLOG.md.

| Habit | Prevents |
|---|---|
| Rename on **both sides** (Java and SQL) in the same step | `validate` failures (BUGLOG #9) |
| Follow a type change everywhere: fields, constructor, getters, callers | Compile errors (#7) |
| Save before building | Building the old file (#4) |
| Read the **last** `Caused by:` first | Chasing consequences instead of the cause (#1, #8) |
| Check that something happened, not only that nothing crashed (`flyway_schema_history`, `psql`) | Silent failures (#3) |
| Never edit an applied migration once it's shared, not even a comment | Checksum mismatches (#5) |
| Always pair `CHECK` with `NOT NULL` | NULLs slipping past constraints (#6) |
| Start Docker Desktop (and `docker compose up -d`) before running the app | Connection refused (#8) |
| Edit the existing block, don't paste a second copy | Duplicate config (#2) |

## ✍️ Found myself
| Concept | In one line | Why it matters / gotcha |
|---|---|---|
| | | |
