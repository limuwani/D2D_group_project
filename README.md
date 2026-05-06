**Dream To Digital**
Customer
Primary Key: customer_id (integer, NOT NULL)

Attributes:

customer_name (varchar(30), NOT NULL) → first name

customer_surname (varchar(30), NOT NULL) → last name

encrypted_password (varchar(255), NOT NULL) → stored securely

email (varchar(100), NOT NULL) → contact email

phone_number (varchar(20), optional) → contact number

date_joined (timestamp, default CURRENT_TIMESTAMP) → when the customer registered

2. Orders
Primary Key: order_id (integer, NOT NULL)

Foreign Keys:

customer_id → references Customer

staff_id → references Staff

restaurant_id → references Restaurant

Attributes:

status (varchar(15), default 'pending') → order state

created_at (timestamp, default CURRENT_TIMESTAMP) → time order was placed

3. Ratings
Primary Key: rating_id (integer, NOT NULL)

Foreign Keys:

order_id → references Orders

customer_id → references Customer

Attributes:

thumbs (boolean, NOT NULL) → thumbs‑up/thumbs‑down feedback

4. Restaurant
Primary Key: restaurant_id (integer, NOT NULL)

Attributes:

name (varchar(100), NOT NULL) → restaurant name

address (text) → physical location

phone_number (varchar(20)) → contact number

email (varchar(100)) → contact email

opening_hours (varchar(50)) → business hours

image_path (text) → logo or image path

5. Staff
Primary Key: staff_id (integer, NOT NULL)

Foreign Key:

restaurant_id → references Restaurant

Attributes:

staff_fname (varchar(30), NOT NULL) → first name

staff_lname (varchar(30), NOT NULL) → last name

phone_no (varchar(15)) → contact number

role (varchar(20)) → job role (e.g., waiter, chef)

status (varchar(15), default 'active') → employment status

staff_email (text, default generated from staff_id) → staff email

created_at (timestamp, default CURRENT_TIMESTAMP) → record creation time

password_hash (varchar(100), NOT NULL) → encrypted password

last_login (timestamp, default CURRENT_TIMESTAMP) → last login time

🔗 Relationships in Words
Customer to Orders

One customer can place many orders.

Each order belongs to exactly one customer.

Orders to Restaurant

Many orders can be placed at one restaurant.

Each order is tied to exactly one restaurant.

Restaurant to Staff

A restaurant employs many staff members.

Each staff member belongs to one restaurant.

Orders to Staff

Many orders can be handled by one staff member.

Each order is assigned to exactly one staff member.

Orders to Ratings

Each order may have zero or one rating.

Each rating is tied to exactly one order.

Customer to Ratings

A customer can give many ratings.

Each rating belongs to exactly one customer.
