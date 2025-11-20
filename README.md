# 🍽️ Ovvium — Full-Stack Mobile Ordering & Payments Platform for Restaurants  
### *React Native · Java/Spring Boot · Electron POS · NFC/QR Table System · Startup Execution*

> **Note:** This repository documents Ovvium, a real startup I founded and built end-to-end.  
> The company no longer operates, but the project represents a complete product vision,  
> technical architecture, and execution across mobile, web, POS, backend, and business.

---

# 🚀 Overview

**Ovvium** was a full-stack restaurant technology platform designed to enable diners to
order, split bills, and pay directly from their smartphones — all fully integrated with
the restaurant's POS and operations.

## 🎬 See Ovvium in Action

> **📱 Complete mobile demo** showing NFC connection, Face ID authentication, ordering flow, bill splitting, tips, and integrated payments

[**Watch Full Demo (2m20s)**](doc/video/app-in-action.mp4)

**Features demonstrated:**
- 📱 NFC table connection workflow
- 🔐 Face ID biometric authentication
- 🛒 Real-time menu browsing and ordering
- 💰 Advanced bill splitting capabilities
- 🎯 Tip selection and payment integration
- 💳 Complete checkout and payment flow

---

## 💡 The Vision

A frictionless experience where a customer sits at a table, scans an NFC/QR tag attached
to the device on the table, joins their group, and immediately interacts with the live order.

I founded the project, designed the business model, built most of the product, and led a
team of four across engineering and commercial development.

Although Ovvium ultimately closed during the COVID crisis, the project represents a
**complete end-to-end startup execution** across product, technology, design, and operations.

---

# 🧠 Business Concept

### 🔹 How Ovvium worked
- Each restaurant table had a custom physical device with:
  - **NFC tag**
  - **QR code**
- Diners scanned the tag with the mobile app to **join the table session**.
- Every diner had **their own individual account** inside the shared table.
- The menu was loaded live from the restaurant's catalog.
- Diners could:
  - Browse products
  - Send orders to the restaurant
  - See other guests' orders
  - Split bills individually or collectively
  - Pay directly via integrated payment gateway

### 📟 Physical Table Device
*Custom-designed hardware for seamless table-to-app connection*

<div align="center">
  <img src="doc/images/branding/nfc-qr-device.jpeg" alt="NFC/QR Table Device" width="400"/>
  <br>
  <em>Physical device placed on each restaurant table with integrated NFC tag and QR code</em>
</div>  

### 🔹 Restaurant Side
- A dedicated **Electron POS** that connected to ticket printers, cash drawers, and hardware.
- A **kitchen view** and **bar view** for marching orders.
- A **web commander** (web POS) for staff using tablets/PDAs.
- A **backoffice** to manage:
  - Menu & products  
  - Categories  
  - Restaurant configuration  
  - Billing & invoices  

### 🔹 Monetization
- SaaS fee per restaurant installation  
- Additional revenue from **tips**, which were suggested during checkout  
  and shared between restaurant and platform.

---

# 💥 Why It Failed (and What I Learned)

We launched at the worst possible moment:  
**start of COVID**, when restaurants were collapsing and unwilling to adopt new tools,  
even though the product perfectly matched the contactless environment.

We were unable to close any commercial deals.  
Despite this, I consider Ovvium one of the most valuable learning experiences of my life.

I learned to:
- Pitch a product  
- Lead a multidisciplinary team  
- Build a brand from scratch  
- Execute under pressure  
- Architect a complex, multi-device platform  
- Integrate payments, hardware, and mobile tech  
- Assume full responsibility from end to end

I never hide failures. **Ovvium made me a far better engineer, leader, and builder.**

---

# 👤 My Role (Founder, CEO, CTO)

I acted as **everything** the company needed:

- **Founder & CEO**  
- **CTO** & lead engineer  
- **Product designer**  
- **UX/UI + brand designer** (I designed the company’s logo, branding, devices)  
- **Sales & business development**  
- **Team coordinator**  
- **Hardware concept designer** (table devices with NFC/QR)  
- **Mobile developer**  
- **Backend & API engineer**  
- **POS developer**  

I also convinced three other professionals (another engineer + two in business/commercial roles)  
to invest time and money into the project.

---

# 🛠️ Technical Architecture

Ovvium consisted of **four major systems**, all developed simultaneously:

---

## 1️⃣ Mobile App — React Native
*(Built before React Hooks existed)*

Features:
- Table session connection (NFC or QR)
- Live menu display
- Create & send orders
- Individual account per diner
- Bill splitting
- Integrated payments
- Tip workflow
- Real-time updates via WebSocket

Tech:
- React Native
- Redux
- REST API + JWT auth

### 📱 Mobile App Screenshots

| Login & Onboarding | Menu & Ordering | User Experience |
|-------------------|-----------------|------------------|
| ![Login](doc/images/app/app-1.jpeg) | ![Menu](doc/images/app/app-2.jpeg) | ![Orders](doc/images/app/app-3.jpeg) |
| ![Onboarding 1](doc/images/app/on-boarding-1.jpeg) | ![Menu Details](doc/images/app/app-4.jpeg) | ![Profile](doc/images/app/app-5.jpeg) |
| ![Onboarding 2](doc/images/app/on-boarding-2.jpeg) | ![Cart](doc/images/app/app-6.jpeg) | ![Payments](doc/images/app/app-7.jpeg) |

Additional screens: [Bill Flow](doc/images/app/app-8.jpeg) | [Settings](doc/images/app/app-9.jpeg)

---

## 2️⃣ POS — Electron + React

A fully functional Point of Sale with:
- Hardware access (ticket printers, cash drawer)
- Bar/Kitchen screens
- Order marching
- Multi-terminal support
- Real-time updates

Tech:
- Electron
- React
- Redux
- Direct hardware integration
- Restaurant session sync via WebSockets

### 🖥️ POS System Screenshots

| Main Interface | Kitchen Management | Order Processing |
|----------------|-------------------|------------------|
| ![POS Main](doc/images/pos/pos-1.jpeg) | ![Kitchen View](doc/images/pos/pos-2.jpeg) | ![Orders](doc/images/pos/pos-3.jpeg) |
| ![Table Layout](doc/images/pos/pos-4.jpeg) | ![Order Details](doc/images/pos/pos-5.jpeg) | ![Payment](doc/images/pos/pos-6.jpeg) |
| ![Menu Management](doc/images/pos/pos-7.jpeg) | ![Real-time Updates](doc/images/pos/pos-8.jpeg) | ![Reports](doc/images/pos/pos-9.jpeg) |



---

## 3️⃣ Backoffice — Admin Panel

Full management panel for restaurant owners:

- Menu management
- Product configuration
- Restaurant setup
- Billing & invoices
- Operator management
- Analytics (basic)

Tech:
- React
- REST APIs
- Secured with JWT

### 🔧 Brand & Design Assets

| Logo & Branding | Design System | Marketing Materials |
|----------------|---------------|-------------------|
| ![Brand 1](doc/images/branding/brand-1.jpeg) | ![Brand 2](doc/images/branding/brand-2.jpeg) | ![Brand 3](doc/images/branding/brand-3.jpeg) |
| ![Brand 4](doc/images/branding/brand-4.jpeg) | ![Brand 5](doc/images/branding/brand-5.jpeg) | ![Brand 6](doc/images/branding/brand-6.jpeg) |

*Complete brand identity designed from scratch, including logo, color palette, typography, and marketing materials.*

---

## 4️⃣ Backend — Java + Spring Boot

The backend connected all systems together:

- Spring Boot
- Spring Security (JWT)
- JPA (Hibernate)
- PostgreSQL
- WebSockets for POS/mobile sync
- Payment gateway integration
- Fully REST-based API

---

# 💻 Source Code

## 📱 Mobile App Source Code
**[🔗 View Complete Mobile App Code](ovvium-app/)**

The mobile app source code includes:
- **React Native + TypeScript** components and screens
- **Redux** state management architecture
- **NFC integration** and hardware communication
- **Payment flows** and bill splitting logic
- **Biometric authentication** implementation
- **Complete package.json** with all dependencies

## ⚙️ Backend Source Code
**[🔗 View Backend Source Code](ovvium-backend/src/main/java/com/ovvium/services/)**

Key architectural decisions from 2018-2020:
- **Domain-driven design** with rich business objects and domain events
- **Spring Boot + Security** with JWT + API Key authentication for multi-client access
- **Event-driven architecture** for decoupled business logic and async processing
- **Distributed caching** with Hazelcast for POS synchronization across terminals
- **Complex payment processing** with commission calculations and provider integrations
- **WebSocket integration** for real-time bill updates between mobile and POS
- **Multi-tenant architecture** supporting multiple restaurant customers
- **AWS integration** for media storage and scalable infrastructure

**Sample components:**
- [Security Configuration](ovvium-backend/src/main/java/com/ovvium/services/app/config/SecurityConfig.java) - JWT + API Key authentication
- [Bill Domain Model](ovvium-backend/src/main/java/com/ovvium/services/model/bill/Bill.java) - Complex business logic
- [Payment Processing](ovvium-backend/src/main/java/com/ovvium/services/model/payment/PaymentOrder.java) - Financial transactions
- [API Controllers](ovvium-backend/src/main/java/com/ovvium/services/web/controller/bff/v1/) - REST API endpoints

## 🖥️ POS System Source Code
**[🔗 View POS Source Code](ovvium-pos/src/app/)**

Electron-based Point of Sale system with:
- **React + TypeScript** desktop application architecture
- **Real-time kitchen management** with order status tracking and timing
- **Hardware integration** for thermal printers and cash drawers
- **Complex bill splitting** and payment processing workflows
- **Multi-location support** for restaurant chains and franchises
- **Material-UI + Bootstrap** professional interface design
- **Redux state management** with persistence for offline capability

**Key components:**
- [Kitchen Management](ovvium-pos/src/app/components/Kitchen/KitchenView.tsx) - Real-time order processing and status tracking
- [Bill Processing](ovvium-pos/src/app/components/Bill/BillView.tsx) - Payment flows and bill splitting logic
- [App Architecture](ovvium-pos/src/app/App.tsx) - Route management and authentication flows
- [Redux Configuration](ovvium-pos/src/app/config/ReduxConfig.ts) - State management setup

> **Note:** This code represents my architectural thinking from 5-7 years ago. 

> **Security Note:** Configuration files, build scripts, environment variables, API keys, and deployment configurations have been omitted from this repository for security and simplification purposes. The core application logic and architecture are fully represented.



---

# 🧱 High-Level Architecture Diagram

```mermaid
flowchart LR

    subgraph Mobile App
        A[React Native App]
    end

    subgraph Restaurant POS
        B[Electron POS]
        C[Web POS]
    end

    subgraph Backend
        D[Spring Boot API]
        E[PostgreSQL]
        F[Payment Gateway]
        G[WebSocket Hub]
    end

    subgraph Backoffice
        H[React Admin Panel]
    end

    A -- JWT / REST --> D
    B -- JWT / REST --> D
    C -- JWT / REST --> D
    H -- JWT / REST --> D

    A -- WebSocket --> G
    B -- WebSocket --> G
    C -- WebSocket --> G

    D --> E
    D --> F
```

## 📈 What This Project Demonstrates

- ✔ **Ability to build complete products**, not only code  
- ✔ **Full-stack engineering** across mobile, backend, and desktop  
- ✔ **Hardware integration** with NFC and QR workflows  
- ✔ **Leadership** as founder, CTO, andaqu product owner  
- ✔ **Execution under real-world constraints** (COVID era)  
- ✔ **Architectural thinking** and multi-system orchestration  
- ✔ **Applied entrepreneurship** and multidisciplinary coordination  

Even though Ovvium did not succeed commercially, it represents a **complete founder–engineer journey** and a solid demonstration of senior ability and ownership.

---

## 📬 Contact

If you'd like to discuss the architecture, design decisions, or startup execution aspects, feel free to reach out:

**jordi.cortes.bravo@gmail.com**  
**Spain (Remote)**


