create table administrators (
    id uuid primary key,
    full_name varchar(120) not null,
    email varchar(140) not null unique,
    password_hash varchar(255) not null,
    role varchar(20) not null,
    active boolean not null default true,
    last_login_at timestamp,
    created_at timestamp not null
);

create table guests (
    id uuid primary key,
    first_name varchar(80) not null,
    last_name varchar(80) not null,
    email varchar(140) not null unique,
    phone varchar(40),
    created_at timestamp not null
);

create table loyalty_accounts (
    id uuid primary key,
    membership_number varchar(40) unique,
    points_balance int not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    guest_id uuid unique,
    constraint fk_loyalty_guest foreign key (guest_id) references guests(id)
);

create table loyalty_ledger (
    id uuid primary key,
    account_id uuid not null,
    type varchar(20) not null,
    points int not null,
    description varchar(255),
    reference_code varchar(60),
    occurred_at timestamp not null,
    constraint fk_ledger_account foreign key (account_id) references loyalty_accounts(id)
);

create table reservations (
    id uuid primary key,
    confirmation_code varchar(80) unique,
    check_in_date date not null,
    check_out_date date not null,
    adults int not null,
    children int not null,
    status varchar(20) not null,
    subtotal numeric(12,2) not null,
    taxes numeric(12,2) not null,
    discounts numeric(12,2) not null,
    total numeric(12,2) not null,
    deposit_required numeric(12,2) not null,
    deposit_paid numeric(12,2) not null,
    loyalty_applied boolean not null,
    notes varchar(500),
    created_at timestamp not null,
    updated_at timestamp not null,
    guest_id uuid not null,
    constraint fk_res_guest foreign key (guest_id) references guests(id)
);

create table reservation_rooms (
    id uuid primary key,
    room_type varchar(20) not null,
    quantity int not null,
    nightly_rate numeric(10,2) not null,
    guests_supported int not null,
    reservation_id uuid not null,
    constraint fk_room_reservation foreign key (reservation_id) references reservations(id)
);

create table reservation_addons (
    id uuid primary key,
    code varchar(30) not null,
    name varchar(80) not null,
    unit_price numeric(10,2) not null,
    per_night boolean not null,
    nights int not null,
    reservation_id uuid not null,
    constraint fk_addon_reservation foreign key (reservation_id) references reservations(id)
);

create table payments (
    id uuid primary key,
    reservation_id uuid not null,
    method varchar(20) not null,
    amount numeric(12,2) not null,
    refund boolean not null,
    reference varchar(80),
    actor_email varchar(140),
    processed_at timestamp not null,
    constraint fk_payment_reservation foreign key (reservation_id) references reservations(id)
);

create table feedback (
    id uuid primary key,
    reservation_id uuid unique not null,
    rating int not null,
    comment varchar(1000),
    sentiment varchar(20),
    submitted_at timestamp not null,
    constraint fk_feedback_reservation foreign key (reservation_id) references reservations(id)
);

create table waitlist (
    id uuid primary key,
    guest_name varchar(160) not null,
    phone varchar(40),
    email varchar(140),
    desired_room_type varchar(20) not null,
    start_date date not null,
    end_date date not null,
    status varchar(20) not null,
    notes varchar(400),
    created_at timestamp not null,
    updated_at timestamp not null
);

create table activity_log (
    id uuid primary key,
    timestamp timestamp not null,
    actor varchar(140),
    action varchar(40) not null,
    entity_type varchar(60),
    entity_identifier varchar(80),
    message varchar(500)
);

insert into administrators (id, full_name, email, password_hash, role, active, created_at)
values
    (random_uuid(), 'Blue Harbor Admin', 'admin@blueharbor.com',
     '{bcrypt}$2a$10$RQ8BbNJvbaTm/7I0PWw5Qu8WdT2aPbmzXG5PHUBWDKFEU85h5DEhm', 'ADMIN', true, current_timestamp),
    (random_uuid(), 'Blue Harbor Manager', 'manager@blueharbor.com',
     '{bcrypt}$2a$10$IX1BYMFjFBvCjCCiu2EvPesz8s9i6dqbBDw177lGgH1q3Ob0Cujcy', 'MANAGER', true, current_timestamp);
