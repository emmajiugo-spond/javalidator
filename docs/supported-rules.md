# Supported Validation Rules

Javalidator comes with **32 built-in validation rules**. All rules can be combined using the pipe `|` separator for complex validations.

## Quick Reference Tables

### Basic Validation Rules

| Rule | Syntax | Description | Example |
|------|--------|-------------|---------|
| **required** | `required` | Field must not be null or blank | `@Rule("required")` |
| **min** | `min:n` | String must have at least n characters | `@Rule("min:3")` |
| **max** | `max:n` | String must not exceed n characters | `@Rule("max:50")` |
| **email** | `email` | Must be a valid email format | `@Rule("email")` |
| **numeric** | `numeric` | Must be a number type | `@Rule("numeric")` |
| **gt** | `gt:n` | Number must be greater than n | `@Rule("gt:0")` |
| **lt** | `lt:n` | Number must be less than n | `@Rule("lt:100")` |
| **gte** | `gte:n` | Number must be ≥ n | `@Rule("gte:18")` |
| **lte** | `lte:n` | Number must be ≤ n | `@Rule("lte:65")` |
| **between** | `between:min,max` | Number must be between min and max | `@Rule("between:18,65")` |
| **regex** | `regex:pattern` | Must match regex pattern | `@Rule("regex:^[A-Z]{2}\\d{4}$")` |
| **in** | `in:val1,val2` | Must be one of specified values | `@Rule("in:admin,user,guest")` |
| **size** | `size:n` | Must be exactly n characters/items | `@Rule("size:5")` |

### String Format Rules

| Rule | Syntax | Description | Example |
|------|--------|-------------|---------|
| **alpha** | `alpha` | Only alphabetic characters | `@Rule("alpha")` |
| **alpha_num** | `alpha_num` | Only alphanumeric characters | `@Rule("alpha_num")` |
| **digits** | `digits:n` or `digits:min,max` | Must contain exactly n digits, or between min and max digits | `@Rule("digits:5")` or `@Rule("digits:10,11")` |
| **url** | `url` | Must be a valid URL | `@Rule("url")` |
| **ip** | `ip` | Must be a valid IPv4 address | `@Rule("ip")` |
| **uuid** | `uuid` | Must be a valid UUID | `@Rule("uuid")` |
| **json** | `json` | Must be valid JSON | `@Rule("json")` |
| **enum** | `enum` with `enumClass` param | String must match enum constant | `@Rule(value="enum", enumClass=Status.class)` |

### Date/Time Rules

| Rule | Syntax | Description | Example |
|------|--------|-------------|---------|
| **date** | `date` or `date:format` | Must be a date type or match format | `@Rule("date")` or `@Rule("date:dd-MM-yyyy")` |
| **before** | `before:yyyy-MM-dd` | Date must be before specified date | `@Rule("before:2025-12-31")` |
| **after** | `after:yyyy-MM-dd` | Date must be after specified date | `@Rule("after:2024-01-01")` |
| **future** | `future` | Date must be in the future | `@Rule("future")` |
| **past** | `past` | Date must be in the past | `@Rule("past")` |

### Conditional & Comparison Rules

| Rule | Syntax | Description | Example |
|------|--------|-------------|---------|
| **required_if** | `required_if:field,value` | Required if another field has value | `@Rule("required_if:country,USA")` |
| **required_unless** | `required_unless:field,value` | Required unless another field has value | `@Rule("required_unless:payment,cash")` |
| **same** | `same:field` | Must match another field's value | `@Rule("same:password")` |
| **different** | `different:field` | Must differ from another field's value | `@Rule("different:oldPassword")` |

### Other Rules

| Rule/Annotation | Syntax | Description | Example |
|-----------------|--------|-------------|---------|
| **nullable** | `nullable` | Explicitly allows null values | `@Rule("nullable\|email")` |
| **distinct** | `distinct` | Array/collection elements must be unique | `@Rule("distinct")` |
| **@RuleCascade** | `@RuleCascade` | Validates nested objects and collections | `@RuleCascade Address address` |

## Combining Rules

Multiple rules can be combined using the pipe `|` separator:

```java
public record UserDTO(
    @Rule("required|min:3|max:20")
    String username,

    @Rule("required|email|max:100")
    String email,

    @Rule("required|numeric|between:18,120")
    Integer age
) {}
```

## Custom Error Messages

Override default error messages using multiple `@Rule` annotations:

```java
public record UserDTO(
    @Rule(value = "required", message = "Username is required")
    @Rule(value = "min:3", message = "Username must be at least 3 characters")
    String username
) {}
```

## Detailed Rule Examples

### Date Rule with Format

```java
public record EventDTO(
    @Rule("date")
    LocalDate eventDate,  // Type checking for date objects

    @Rule("date:dd-MM-yyyy")
    String dateString  // Format validation for strings
) {}
```

### Enum Rule

Validates that a String matches a valid enum constant:

```java
public record OrderDTO(
    @Rule(value = "enum", enumClass = OrderStatus.class)
    String status  // Must match one of the enum constants
) {}
```

### Conditional Rules

```java
public record PaymentDTO(
    String paymentMethod,

    @Rule("required_if:paymentMethod,card")
    String cardNumber,  // Required only when paymentMethod is "card"

    @Rule("required_unless:paymentMethod,cash")
    String billingAddress  // Required unless paymentMethod is "cash"
) {}
```

### Field Comparison Rules

```java
public record PasswordChangeDTO(
    @Rule("required")
    String currentPassword,

    @Rule("required|min:8|different:currentPassword")
    String newPassword,

    @Rule("required|same:newPassword")
    String confirmPassword
) {}
```

### Digits Rule

```java
public record SecurityDTO(
    @Rule("required|digits:4")
    String pin,  // Exactly 4 digits

    @Rule("required|digits:10,11")
    String phone  // Between 10 and 11 digits
) {}
```

### Distinct Rule

```java
public record OrderDTO(
    @Rule("required|distinct")
    List<String> productIds  // All elements must be unique
) {}
```

### Nested Validation with @RuleCascade

```java
public record Address(
    @Rule("required")
    String street,

    @Rule("required|digits:5")
    String zipCode
) {}

public record User(
    @Rule("required|email")
    String email,

    @RuleCascade  // Validates nested Address
    Address address,

    @RuleCascade  // Validates each item in collection
    List<Address> alternateAddresses
) {}

// Error paths show nested structure:
// "address.street: The street field is required."
// "alternateAddresses[0].zipCode: The zipCode must be exactly 5 digits."
```

## Null Value Handling

- **required**: Fails if value is `null` or blank
- **All other rules**: Skip validation if value is `null`

```java
@Rule("gte:18")
Integer age;  // null is OK, but if provided must be >= 18

@Rule("required|gte:18")
Integer age;  // null fails, value < 18 also fails
```

---

[← Back to README](../README.md) | [Custom Rules Guide →](custom-rules.md)