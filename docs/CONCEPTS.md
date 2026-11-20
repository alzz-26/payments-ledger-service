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

**Questions I should be able to answer:**
1. Why `EnumType.STRING` and not `ORDINAL`?
2. Why is there no `balance` column on `accounts`?
3. What should happen if someone tries to `UPDATE` or `DELETE` a ledger entry?

## ✍️ Found myself
| Concept | In one line | Why it matters / gotcha |
|---|---|---|
| | | |
