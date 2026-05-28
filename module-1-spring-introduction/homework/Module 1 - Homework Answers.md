# Module 1 - Homework Answers

## 1. List of Annotations Learned So Far

### Spring Framework / Spring Boot Annotations

* `@SpringBootApplication`
* `@Component`
* `@Autowired`
* `@Qualifier`

### Additional Concepts Learned

* Dependency Injection
* Beans
* Constructor Injection
* Interfaces & Implementations
* Auto Configuration
* Maven Basics

---

# 2. Scenarios Where Spring Framework is Useful

## ✅ Enterprise Applications

Spring Framework is widely used in enterprise-level applications where:

* scalability
* security
* maintainability

are very important.

Examples:

* Banking Systems
* ERP Systems
* E-commerce Backends

---

## ✅ Strong Type Safety

Java provides:

* compile-time checking
* better type safety
* better maintainability

which is useful for large-scale applications.

---

## ✅ Better Multithreading Support

Java has strong support for:

* multithreading
* concurrency
* parallel processing

which helps in high-performance backend systems.

---

## ✅ Mature Ecosystem

Spring provides support for:

* Security
* Transactions
* REST APIs
* Microservices
* Dependency Injection

---

## ✅ Large Scale Backend Systems

Spring Framework is useful when:

* application size is large
* multiple developers are working together
* long-term maintainability is required

---

# 3. Scenarios Where Spring Boot is More Useful Than Spring Framework

## ✅ Faster Development

Spring Boot reduces boilerplate configuration and helps developers build applications quickly.

---

## ✅ Auto Configuration

Spring Boot automatically configures many things internally, reducing manual setup.

Examples:

* database configuration
* server setup
* dependency management

---

## ✅ Embedded Server

Spring Boot provides embedded servers like:

* Tomcat
* Jetty

No need to deploy WAR files manually.

---

## ✅ Easier Dependency Management

Using starter dependencies like:

* `spring-boot-starter-web`
* `spring-boot-starter-data-jpa`

dependency management becomes easier.

---

## ✅ Production Ready Features

Spring Boot provides:

* Actuator
* Monitoring
* Metrics
* Health Checks

---

## ✅ Better for REST APIs & Microservices

Spring Boot is highly preferred for:

* REST APIs
* Microservices
* cloud-based backend applications

because development and setup become much faster.

---

# 4. Alice and her Bakery (Practical Homework)

## ✅ Objective

Implemented a Spring Boot application demonstrating:

* Dependency Injection
* Interfaces & Implementations
* Bean Management
* Constructor Injection
* `@Qualifier` usage

---

## ✅ Classes & Interfaces Created

### Interfaces

* `Frosting`
* `Syrup`

### Implementations

#### Frosting Implementations

* `ChocolateFrosting`
* `StrawberryFrosting`

#### Syrup Implementations

* `ChocolateSyrup`
* `StrawberrySyrup`

### Main Business Class

* `CakeBaker`

### Application Class

* `Module1HomeworkApplication`

---

## ✅ Concepts Used

* `@Component`
* `@Qualifier`
* Constructor Injection
* Spring Beans
* Dependency Injection

---

## ✅ Output

The application successfully injects frosting and syrup dependencies into the `CakeBaker` class and calls the `bakeCake()` function.

Example Output:

```text id="3uq4jm"
Cake Baker...
Chocolate Frosting
Chocolate Syrup
```

---

# Conclusion

This module helped in understanding:

* Spring Fundamentals
* Bean Creation & Management
* Dependency Injection
* Spring Boot Project Structure
* Constructor Injection Best Practices
