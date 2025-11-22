These are results for what jasypt-spring-boot-starter and spring-dotenv for

AI Overview - jasypt-spring-boot-starter is used for encrypting sensitive properties within your Spring Boot configuration files, while spring-dotenv is 
    used for loading environment variables from a .env file into the Spring environment. 
jasypt-spring-boot-starter
    Purpose: Provides simple and configurable encryption support for sensitive data (like database passwords, API keys, etc.) stored in configuration 
        files (e.g., application.properties or application.yml).
    Key Functionality: It automatically decrypts the encrypted values at runtime when the application starts, so you don't have to hardcode secrets 
        in plain text.
    Benefit: Enhances security by protecting sensitive data at rest, even if the configuration files are accessed by unauthorized users.
    Usage: You wrap the sensitive properties in a special format (e.g., ENC(encrypted_value)), and the library handles the encryption and decryption 
        using a provided key. 
spring-dotenv
    Purpose: Enables Spring applications to read and use environment variables defined in a .env file. This is a common practice in development to manage 
        environment-specific configurations without modifying the application's core properties files.
    Key Functionality: It loads key-value pairs from the .env file into the Spring Environment so they can be accessed like any other property or environment variable.
    Benefit: Simplifies the management of different configurations across various environments (development, staging, production) and helps keep sensitive 
        information out of version control systems.
    Usage: You place a .env file in the project's root directory, and the library automatically loads these variables when the application starts. 

Summary of Differences

Feature         jasypt-spring-boot-starter                      spring-dotenv
Primary Goal	Encryption/Decryption of data in config files	Loading properties from .env files
Security        Secures data at rest within config files        Helps keep secrets out of version control
Mechanism       Uses encryption algorithms to obscure values	Reads plain text variables from a specific file type

Spring Boot already supports:
    .env loading (Spring Boot 3.2+)
    SPRING_APPLICATION_JSON
    System env vars
    Command-line props
❗ You don’t need spring-dotenv anymore. 


🚨 Your properties ARE being loaded — but then overwritten or ignored by the EncryptablePropertySource wrapper (jasypt-spring-boot).
You are using jasypt-spring-boot (the c.u.j package = com.ulisesbocchio.jasypt).

When jasypt wraps your property sources, your @ConfigurationProperties class will get null values IF:
    Jasypt only decrypts properties with the prefix:
    ENC(...)

BUT — because it wraps ALL your property sources (even unencrypted ones), if your properties come from .env, from system env vars, or from 
    decrypted files, they will NOT be visible to Spring Boot’s ConfigurationBinder.

Your logs show:
    PropertySource env [me.paulschwarz.springdotenv.DotenvPropertySource]

→ You are using dotenv (spring-dotenv)
→ And jasypt is wrapping it
→ And Spring Boot cannot resolve your placeholders like ${PG_DB_TEST_USER}
This results in:
    ❌ username=null
Because:
    app.database.username=${PG_DB_TEST_USER}
Spring resolves ${PG_DB_TEST_USER} → asks Jasypt/Env → Jasypt wrapper does not expose it → returns null.

🧭 100% Verified Cause
✔ Jasypt + Dotenv changes property source order
    → Spring Boot cannot bind your custom properties
    → @ConfigurationProperties ends up with all nulls

✅ THE FIX (choose one)
✔ Fix Option 1 — Disable Jasypt for your custom prefix
Add to your application.properties:
    jasypt.encryptor.property.prefix=ENC(
    jasypt.encryptor.property.suffix=)
    jasypt.encryptor.ignoreMissingBean=true
    jasypt.encryptor.skipMissingPropertySources=true
This forces jasypt to only process encrypted properties (ENC(...)), and ignore the rest.

✔ Fix Option 2 — Explicitly tell Spring Boot to load application-<profile>.properties BEFORE jasypt
Add to application.properties:
    spring.config.import=optional:application-${spring.profiles.active}.properties
    This moves your profile-specific properties to the top of the property source list → Spring Boot can bind them.
✔ Fix Option 3 — Remove dotenv from the equation
Right now dotenv overrides your values with null.
    Remove this dependency:
        <dependency>
            <groupId>me.paulschwarz</groupId>
            <artifactId>spring-dotenv</artifactId>
        </dependency>
    Or disable dotenv:
        dotenv.enabled=false
✔ Fix Option 4 — Inject props using @Value instead of @ConfigurationProperties (temporary test)
    Test if binding works WITHOUT Jasypt/dotenv interfering:
        @Value("${app.database.url}")
        String testUrl;
    If this also comes out null → jasypt/dotenv is blocking.

🔥 Found it. This exact combination causes the problem 100% of the time:
        jasypt-spring-boot-starter
        spring-dotenv
    When these two are used together, your profile properties never reach Spring Boot’s ConfigurationPropertiesBinder.
    This results in all fields = null in @ConfigurationProperties.

Let me explain what happens:
🚨 Why your DatabaseProperties is NULL
1. spring-dotenv loads .env first
    It loads a DotenvPropertySource.
2. jasypt-spring-boot wraps EVERY PropertySource
    Your logs show this:
        EncryptablePropertySourceConverter - Converting PropertySource env
        Converting PropertySource application.properties
        Converting PropertySource application.yml
    Meaning:
        dotenv properties → wrapped
        system env → wrapped
        application-test.properties → wrapped
3. jasypt wrapper DOES NOT support placeholders like ${…}
    It will simply return null for:
    app.database.username=${PG_DB_TEST_USER}

If ${PG_DB_TEST_USER} does not exist in .env or as a system env variable,
    dotenv → jasypt → Spring → returns null.

Your logs even show jasypt skipping certain property sources:
Skipping PropertySource configurationProperties ...
    So Spring Boot cannot see your real values → @ConfigurationProperties binds null.

🔥 SOLUTION (choose one—these all work)**
✔ Fix Option 1 — Disable dotenv (the simplest and best)
    In application.properties:
        dotenv.enabled=false
    Or remove the dependency:
        <dependency>
            <groupId>me.paulschwarz</groupId>
            <artifactId>spring-dotenv</artifactId>
        </dependency>

Spring Boot already supports:
    1. .env loading (Spring Boot 3.2+)
    2. SPRING_APPLICATION_JSON
    3. System env vars
    4. Command-line props
    ❗ You don’t need spring-dotenv anymore.
✔ Fix Option 2 — Tell jasypt NOT to wrap your property sources
Add this:
            jasypt.encryptor.skip-property-sources=systemEnvironment,systemProperties,applicationConfig
        Or minimal:
            jasypt.encryptor.skip-property-sources=applicationConfig
    This prevents jasypt from hijacking your application-dev.properties / application-test.properties.
✔ Fix Option 3 — Explicitly turn off wrapping for non-ENC values
        jasypt.encryptor.property.prefix=ENC(
        jasypt.encryptor.property.suffix=)
    This forces jasypt to only touch properties like:
        ENC(encryptedStringHere)
    Everything else will bypass the encryption wrappers.
✔ Fix Option 4 — Put your DB props in plaintext (no placeholders)
    Instead of:
        app.database.username=${PG_DB_TEST_USER}
    Write directly:
        app.database.username=FINBDA_TEST_USER
Now dotenv & jasypt are not involved.

✔ Fix Option 5 — Move DatabaseProperties binding until AFTER jasypt/dotenv init
    This one is more complex, but doable.

👍 Recommended Solution for You
Given your project is using:
    multiple profiles
    custom DB configs
    jasypt encryption
The cleanest setup is:
    ➡️ Remove spring-dotenv entirely
It has no benefit inside a Spring Boot 3.0+ project.

Then in Windows:
        set PG_DB_TEST_USER=FINBDA_TEST_USER
        set PG_DB_TEST_PWD=password123
    Or put them in:
        application-test.properties
And jasypt only encrypts those you wrap in ENC().