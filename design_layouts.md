# D2D Ordering System — Design Layout & Navigation Specification

> **Project**: D2D Group Project  
> **Package**: `com.example.d2d`  
> **Document Version**: 1.0  
> **Last Updated**: May 10, 2026

---

## 1. Screen Inventory

The application consists of **14 XML layout files** organized across three functional domains:

| # | Layout File | Screen Title | Domain | Role Access |
|---|-------------|-------------|--------|-------------|
| 1 | `login_page.xml` | Sign In | Authentication | All Users |
| 2 | `sign_up.xml` | Create Account | Authentication | All Users |
| 3 | `secure_account.xml` | Secure Your Account | Authentication | All Users |
| 4 | `terms.xml` | Terms & Conditions | Authentication | All Users |
| 5 | `activity_main.xml` | Main Shell (Nav Host) | Core Framework | All Users |
| 6 | `select_res.xml` | Discover Restaurants | Customer | Customer |
| 7 | `confirm_takeaway.xml` | Confirm Takeaway | Customer | Customer |
| 8 | `cus_order_status.xml` | Active Orders | Customer | Customer |
| 9 | `rate_service.xml` | Rate Service | Customer | Customer |
| 10 | `s_staff_portal.xml` | Staff Portal | Staff | Staff / Waiter |
| 11 | `s_assign_order.xml` | New Order | Staff | Staff / Waiter |
| 12 | `s_active_queue.xml` | Takeaway Queue | Staff | Staff / Waiter |
| 13 | `s_view_feedback.xml` | Staff Profile / Feedback | Staff | Staff / Waiter |
| 14 | `item_restaurant.xml` | Restaurant Card (Reusable) | Component | Customer |

---

## 2. Complete Navigation Flow Graph

```mermaid
graph TD
    subgraph AUTH["🔐 Authentication Flow"]
        A["login_page.xml<br/><b>Sign In</b>"]
        B["sign_up.xml<br/><b>Create Account</b>"]
        C["terms.xml<br/><b>Terms & Conditions</b>"]
        D["secure_account.xml<br/><b>Secure Account</b>"]
    end

    subgraph CORE["⚙️ Core Shell"]
        E["activity_main.xml<br/><b>Main Shell</b><br/>(FrameLayout + BottomNav)"]
    end

    subgraph CUSTOMER["👤 Customer Flow"]
        F["select_res.xml<br/><b>Discover Restaurants</b>"]
        G["confirm_takeaway.xml<br/><b>Confirm Takeaway</b>"]
        H["cus_order_status.xml<br/><b>Active Orders</b>"]
        I["rate_service.xml<br/><b>Rate Service</b>"]
    end

    subgraph STAFF["🧑‍🍳 Staff Flow"]
        J["s_staff_portal.xml<br/><b>Staff Portal</b>"]
        K["s_assign_order.xml<br/><b>New Order</b>"]
        L["s_active_queue.xml<br/><b>Takeaway Queue</b>"]
        M["s_view_feedback.xml<br/><b>Staff Profile</b>"]
    end

    %% Auth Navigation
    A -- "SIGN UP btn" --> B
    B -- "LOGIN btn" --> A
    B -- "Terms & Conditions btn" --> C
    C -- "BACK TO LOGIN btn" --> A
    B -- "Create Account btn<br/>(on valid)" --> F
    A -- "Sign In btn<br/>(on valid)" --> F

    %% Post-Auth Branching
    F -- "Select Restaurant<br/>(Customer role)" --> G
    G -- "Confirm btn" --> H
    G -- "Cancel btn" --> F
    H -- "BROWSE RESTAURANTS btn" --> F
    H -- "Rate collected order" --> I
    I -- "Submit Feedback btn" --> H

    %% Staff Branching
    F -. "Staff role<br/>(email contains 'waiter')" .-> J
    J -- "+ Start New Order btn" --> K
    J -- "VIEW Active Status btn" --> L
    K -- "Initialize Order btn" --> L
    K -- "Cancel btn" --> J
    L -- "+ ADD NEW ORDER btn" --> K
    L -- "View Feedback" --> M
    M -- "BACK TO QUEUE btn" --> L

    %% Back Arrows
    G -- "⬅ Back" --> F
    H -- "⬅ Back" --> F
    I -- "⬅ Back" --> H
    K -- "⬅ Back" --> J
    L -- "⬅ Back" --> J

    %% Styling
    style AUTH fill:#1a1a2e,stroke:#e94560,stroke-width:2px,color:#fff
    style CORE fill:#16213e,stroke:#0f3460,stroke-width:2px,color:#fff
    style CUSTOMER fill:#0f3460,stroke:#53a8b6,stroke-width:2px,color:#fff
    style STAFF fill:#1a1a2e,stroke:#e9c46a,stroke-width:2px,color:#fff
```

---

## 3. Role-Based Access Architecture

```mermaid
graph LR
    subgraph ENTRY["App Launch"]
        START(("🚀 App Start"))
    end

    subgraph AUTH_GATE["Authentication Gate"]
        LOGIN["LoginActivity.java<br/>login_page.xml"]
        SIGNUP["SignUp.java<br/>sign_up.xml"]
    end

    subgraph ROLE_SPLIT["Role Detection"]
        CHECK{"Email contains<br/>'waiter'?"}
    end

    subgraph CUST_ZONE["Customer Zone<br/>BottomNav: bottom_nav_bar.xml"]
        C1["🏠 Home<br/>select_res.xml"]
        C2["📦 Orders<br/>cus_order_status.xml"]
        C3["👤 Profile"]
    end

    subgraph STAFF_ZONE["Staff Zone<br/>BottomNav: staff_nav_bar.xml"]
        S1["🏠 Home<br/>s_staff_portal.xml"]
        S2["📋 Manage<br/>s_active_queue.xml"]
        S3["👤 Profile<br/>s_view_feedback.xml"]
    end

    START --> LOGIN
    LOGIN <--> SIGNUP
    LOGIN --> CHECK
    CHECK -- "No → Customer" --> C1
    CHECK -- "Yes → Staff" --> S1
    C1 <--> C2
    C1 <--> C3
    S1 <--> S2
    S1 <--> S3

    style ENTRY fill:#0d1b2a,stroke:#778da9,color:#fff
    style AUTH_GATE fill:#1b263b,stroke:#e94560,color:#fff
    style ROLE_SPLIT fill:#415a77,stroke:#e0e1dd,color:#fff
    style CUST_ZONE fill:#0f3460,stroke:#53a8b6,color:#fff
    style STAFF_ZONE fill:#1a1a2e,stroke:#e9c46a,color:#fff
```

---

## 4. Bottom Navigation Bar Structure

### 4.1 Customer Navigation (`bottom_nav_bar.xml`)

| Tab | ID | Icon | Destination |
|-----|----|------|-------------|
| Home | `home_button` | `home_icon` | `select_res.xml` |
| Orders | `orders` | `track_order` | `cus_order_status.xml` |
| Profile | `profile` | `profile_icon` | User profile screen |

### 4.2 Staff Navigation (`staff_nav_bar.xml`)

| Tab | ID | Icon | Destination |
|-----|----|------|-------------|
| Home | `staff_home_button` | `home_icon` | `s_staff_portal.xml` |
| Manage | `manage` | `manage_icon` | `s_active_queue.xml` |
| Profile | `staff_profile` | `profile_icon` | `s_view_feedback.xml` |

> [!NOTE]
> `activity_main.xml` hosts both navigation bars (`bottom_navigation` and `bottom_navigation_staff`), toggling visibility based on the authenticated user's role. A shared `FrameLayout` (`content_frame`) serves as the fragment container.

---

## 5. Screen-by-Screen Detail

### 5.1 — Login Page (`login_page.xml`)

| Property | Value |
|----------|-------|
| **Activity** | `LoginActivity.java` |
| **Root Layout** | `LinearLayout` (vertical, centered) |
| **Background** | `@drawable/main` |

**UI Components:**

| Element | ID | Type | Action |
|---------|----|------|--------|
| Email field | `email_edit_text` | `EditText` | User email input |
| Password field | `password_edit_text` | `EditText` | Password input (masked) |
| Forgot Password | `forgot_password` | `Button` | — |
| Sign In | `sign_in_button` | `Button` | Validates → navigates to `select_res` |
| Sign Up | `sign_up_button` | `Button` | Navigates to `sign_up.xml` |

---

### 5.2 — Sign Up (`sign_up.xml`)

| Property | Value |
|----------|-------|
| **Activity** | `SignUp.java` |
| **Root Layout** | `LinearLayout` (vertical, centered) |

**UI Components:**

| Element | ID | Type |
|---------|----|------|
| First Name | `name_edit_text` | `EditText` |
| Last Name | `Surname_edit_text` | `EditText` |
| Email | `signup_email_edit_text` | `EditText` |
| Password | `signup_password_edit_text` | `EditText` |
| Confirm Password | `confirm_password_edit_text` | `EditText` |
| T&C Checkbox | `terms_condions_checkbox` | `CheckBox` |
| Terms & Conditions | `terms_conditions` | `Button` → `terms.xml` |
| Create Account | `signup_submit_button` | `Button` → validates → `select_res` |
| Back to Login | `back_to_login_button` | `Button` → `LoginActivity` |

---

### 5.3 — Terms & Conditions (`terms.xml`)

| Property | Value |
|----------|-------|
| **Root Layout** | `ScrollView` → `LinearLayout` |
| **Sections** | 5 clauses (Service, Conduct, Tracking, Feedback, Privacy) |

**Actions:**

| Element | ID | Action |
|---------|----|--------|
| Checkbox | `terms_conditions_checkbox` | Acknowledge T&C |
| Back to Login | `terms_to_login` | Returns to login screen |

---

### 5.4 — Secure Account (`secure_account.xml`)

| Property | Value |
|----------|-------|
| **Purpose** | One-time security question setup for password recovery |

**UI Components:**

| Element | ID | Type |
|---------|----|------|
| Security Question | `Surname_edit_text` | `EditText` |
| Secret Answer | `Surname_edit_text` | `EditText` |
| Complete Setup | `complete_setup` | `Button` |

---

### 5.5 — Discover Restaurants (`select_res.xml`)

| Property | Value |
|----------|-------|
| **Activity** | `select_res.java` |
| **Purpose** | Browse available restaurants |

**Restaurant Cards:**

| Restaurant | Container ID | Location | Status |
|-----------|-------------|----------|--------|
| Organic Resto | `the_organic_res` | Sandton | Open Now (green badge) |
| Casa Nova | `casanova` | Braamfontein, Juta St | Listed |

---

### 5.6 — Confirm Takeaway (`confirm_takeaway.xml`)

| Element | ID | Action |
|---------|----|--------|
| Back button | `back_to_home` | Returns to restaurant list |
| Confirm Order | `confirm_order` | Proceeds to order tracking |
| Cancel Order | `customer_cancel_order` | Returns to restaurant list |

---

### 5.7 — Customer Order Status (`cus_order_status.xml`)

| Property | Value |
|----------|-------|
| **Purpose** | Track active orders; empty state with "Browse Restaurants" CTA |
| **Order States** | `PROCESSING` (red badge) |

---

### 5.8 — Rate Service (`rate_service.xml`)

| Element | ID | Type |
|---------|----|------|
| Back button | `back_to_orderstatus` | `ImageButton` → order status |
| Thumbs Up | `thumbs_up` | `ImageView` — "EXCEPTIONAL" |
| Thumbs Down | `thumbs_down` | `ImageView` — "SUBPAR" |
| Comments | `comments` | `EditText` |
| Submit | `submit_feedback` | `Button` |

---

### 5.9 — Staff Portal (`s_staff_portal.xml`)

| Element | ID | Action |
|---------|----|--------|
| Back button | `back_btn` | Returns to previous screen |
| Start New Order | `start_order` | Navigates to `s_assign_order.xml` |
| View Active Status | `view_active_status` | Navigates to `s_active_queue.xml` |

---

### 5.10 — Assign New Order (`s_assign_order.xml`)

| Element | ID | Purpose |
|---------|----|---------|
| Back button | `back_btn` | Returns to staff portal |
| Customer Email | `fullname_edit_text` | Link order to customer |
| Restaurant | `selected_restaurant` | Assign restaurant |
| Initialize | `send_to_customer` | Submit order to queue |
| Cancel | `staff_cancel_order` | Return to portal |

---

### 5.11 — Takeaway Queue (`s_active_queue.xml`)

| Property | Value |
|----------|-------|
| **Purpose** | Track order preparation and handover status |
| **Order States** | `IN PREP` (red) → `READY` (orange) → `COLLECTED` (green) |

**Queue Card Actions:**

| Button | ID | Action |
|--------|----|--------|
| Set Ready | `mark_collected_1` | Transitions to READY |
| Collected | `mark_collected_1` | Transitions to COLLECTED |
| Next Status | `mark_ready_2` | Advance order status |
| + Add New Order | — | Navigate to `s_assign_order.xml` |

---

### 5.12 — Staff Feedback View (`s_view_feedback.xml`)

| Property | Value |
|----------|-------|
| **Purpose** | Display staff satisfaction score and feedback history |
| **Metric** | Satisfaction percentage (e.g., "100%") |
| **Back Action** | "BACK TO QUEUE" button → `s_active_queue.xml` |

---

## 6. Activity ↔ Layout Mapping

```mermaid
graph LR
    subgraph Activities["Java Activities"]
        MA["MainActivity.java"]
        LA["LoginActivity.java"]
        SU["SignUp.java"]
        SR["select_res.java"]
        CS["CustomerSignUp.java<br/>(Data Model)"]
    end

    subgraph Layouts["XML Layouts"]
        L1["login_page.xml"]
        L2["sign_up.xml"]
        L3["select_res.xml"]
        L4["activity_main.xml"]
    end

    MA --> L1
    MA --> L2
    LA --> L1
    SU --> L2
    SR --> L3
    MA --> L4

    style Activities fill:#1a1a2e,stroke:#e94560,color:#fff
    style Layouts fill:#0f3460,stroke:#53a8b6,color:#fff
```

> [!IMPORTANT]
> **Unbound Layouts**: The following layouts are designed but do not yet have dedicated Activity classes wiring their navigation logic:
> `confirm_takeaway.xml`, `cus_order_status.xml`, `rate_service.xml`, `secure_account.xml`, `terms.xml`, `s_staff_portal.xml`, `s_assign_order.xml`, `s_active_queue.xml`, `s_view_feedback.xml`

---

## 7. Order Lifecycle State Machine

```mermaid
stateDiagram-v2
    [*] --> INITIALIZED : Staff creates order
    INITIALIZED --> PENDING : Order sent to customer
    PENDING --> CONFIRMED : Customer confirms
    CONFIRMED --> IN_PREP : Kitchen starts
    IN_PREP --> READY : Food prepared
    READY --> COLLECTED : Customer picks up
    COLLECTED --> RATED : Customer submits feedback
    RATED --> [*]

    PENDING --> CANCELLED : Customer cancels
    CANCELLED --> [*]
```

---

## 8. Design System Summary

| Token | Value |
|-------|-------|
| **Primary Background** | `@drawable/main` (gradient) |
| **Card Background** | `@drawable/catagories` (glassmorphic) |
| **Primary Button** | `@drawable/btn_gradient_bg` (gradient, black text) |
| **Input Fields** | `@drawable/edittext_bg` (translucent, white text/hint) |
| **Error State** | `@drawable/edittext_error_style` (red border) |
| **Font (Headlines)** | `@font/archivoblack` |
| **Font (Brand)** | `@font/lobster` |
| **Status: Processing** | `#CD1C18` (Red) |
| **Status: Ready** | `#FFA500` (Orange) |
| **Status: Collected** | `#0BDA51` (Green) |
| **Status: Open** | `@color/green` |
