# 📁 Liquibase Database Migrations

This document describes the Liquibase setup for managing database schema changes in the Ecomera project.

## 📦 Folder Structure
```
src/main/resources/db/changelog/
├── db.changelog-master.xml          # Master file that includes all migrations
└── changes/
    ├── v0.1.0/                       # Sprint 0 - Initial schema
    │   ├── 001-create-users-table.xml
    │   ├── 002-create-tokens-table.xml
    │   ├── 003-create-products-table.xml
    │   ├── 004-create-orders-table.xml
    │   ├── 005-create-order-items-table.xml
    │   └── 006-create-payments-table.xml
    └── v0.2.0/                       # Future migrations
        └── (future changes)
```

## 🧩 Master Changelog

The `db.changelog-master.xml` includes all migrations in order:
```xml
<include file="db/changelog/changes/v0.1.0/001-create-users-table.xml"/>
<include file="db/changelog/changes/v0.1.0/002-create-tokens-table.xml"/>
<!-- ... -->
```

## 🛠 Naming Conventions

### File Names
- Format: `{sequence}-{action}-{entity}-{target}.xml`
- Examples:
  - `001-create-users-table.xml`
  - `002-create-tokens-table.xml`
  - `007-add-email-index-users.xml`
  - `008-alter-users-add-phone.xml`

### ChangeSet IDs
- **MUST match the filename** (without `.xml`)
- Example: File `001-create-users-table.xml` → ID `001-create-users-table`

### Authors
- Use your GitHub username: `youssef`, `ammari-youssef`, etc.

## 🔧 Configuration

In `application.properties`:
```properties
# Liquibase configuration
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.show-summary=SUMMARY

# Let Liquibase manage schema, not Hibernate
spring.jpa.hibernate.ddl-auto=validate
```

## 🧪 Testing Migrations

### Run Migrations
```bash
# Apply all pending migrations (happens automatically on app startup)
mvn spring-boot:run

# Or with Maven Liquibase plugin
mvn liquibase:update
```

### Rollback Migrations
```bash
# Rollback last changeset
mvn liquibase:rollback -Dliquibase.rollbackCount=1

# Rollback to a specific tag
mvn liquibase:rollback -Dliquibase.rollbackTag=v0.1.0
```

### Validate Changelogs
```bash
# Verify changelog integrity
mvn liquibase:validate
```

### View Migration History
```bash
# See all applied changesets
mvn liquibase:history
```

## 🚀 Workflow Guide

### Adding a New Migration

1. **Create new XML file** in appropriate version folder:
```bash
   touch src/main/resources/db/changelog/changes/v0.2.0/007-add-category-table.xml
```

2. **Write the changeset** (see template below)

3. **Add to master changelog**:
```xml
   <include file="db/changelog/changes/v0.2.0/007-add-category-table.xml"/>
```

4. **Test locally**:
```bash
   mvn clean install
   # Check logs for "ChangeSet ... ran successfully"
```

5. **Commit**:
```bash
   git add src/main/resources/db/
   git commit -m "feat(db): add category table migration"
```

### Migration Template
```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <changeSet id="001-create-users-table" author="youssef">
        <preConditions onFail="MARK_RAN">
            <not>
                <tableExists tableName="_user"/>
            </not>
        </preConditions>

        <createTable tableName="_user">
            <column name="id" type="UUID">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <!-- ... other columns -->
        </createTable>

        <rollback>
            <dropTable tableName="_user"/>
        </rollback>
    </changeSet>

</databaseChangeLog>
```
### 📜 Liquibase CLI Commands Summary

        ./mvnw liquibase:update	                                  # Apply all pending changes
        ./mvnw liquibase:rollback -Dliquibase.rollbackCount=1	  # Rollback last change (1 changeSet)
        ./mvnw liquibase:rollback --define liquibase.rollbackCount=1  # Alternative syntax if the one above don't work
        ./mvnw liquibase:history	                                  # View applied changesets
        ./mvnw liquibase:validate	                                  # Verify changelog integrity
        ./mvnw liquibase:tag --define liquibase.tag=tag_name          # Create a tag (bookmark) at the current database state
        ./mvnw liquibase:rollback --define liquibase.tag=tag_name     # Rollback to last bookmark of the database

## 🔒 Rollback Best Practices

**Always include rollback blocks:**
```xml
<changeSet id="add-phone-column-users" author="youssef">
    <addColumn tableName="_user">
        <column name="phone" type="VARCHAR(20)"/>
    </addColumn>
    
    <rollback>
        <dropColumn tableName="_user" columnName="phone"/>
    </rollback>
</changeSet>
```

## 📋 Migration Checklist

Before committing a migration:

- [ ] ChangeSet ID matches filename
- [ ] PreConditions included (prevents re-running)
- [ ] Rollback block defined
- [ ] Tested locally
- [ ] Added to master changelog
- [ ] No hardcoded values (use properties where needed)

## 🐛 Common Issues

### Issue: "Table already exists"
**Solution:** Add preConditions to check table doesn't exist

### Issue: "Checksum mismatch"
**Solution:** Never modify a migration after it's been merged. Create a new migration instead.

### Issue: "Migration fails in CI"
**Solution:** Ensure database is clean (H2 in-memory for tests). Check `ddl-auto=validate`.

## 🎯 Current Schema Version

**v0.1.0** - Initial schema
- User authentication (users, tokens)
- Product catalog (products)
- Order management (orders, order_items, payments)

**Next version:** v0.2.0 (planned)
- Category entity
- Cart & CartItem entities
- Product variants

## 📚 Resources

- [Liquibase Official Docs](https://docs.liquibase.com/)
- [Spring Boot Liquibase Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.liquibase)
- [Liquibase Best Practices](https://www.liquibase.org/get-started/best-practices)