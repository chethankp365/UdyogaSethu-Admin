# UdyogaSethu Admin — Feature Status & Database Field Traceability

This document traces every UI field and action across all 18 Admin Modules directly to its verified database column in Supabase PostgreSQL (`mmzbpeqkfomnywuzoqii`).

---

## Module 1: Overview Dashboard
| UI Card / Widget | Displayed Metric / Field | Backing Database Table & Expression | Verification Status |
| :--- | :--- | :--- | :--- |
| **Total Registered Users** | Count of all users | `COUNT(*) FROM public.profiles` | Verified |
| **Active Workers** | Count of active job seekers | `COUNT(*) FROM public.worker_profiles wp JOIN public.profiles p ON p.id = wp.user_id WHERE p.is_suspended = false` | Verified |
| **Service Providers** | Count of employers/households | `COUNT(*) FROM public.provider_profiles` | Verified |
| **Registered Farmers** | Count of active farmers | `COUNT(*) FROM public.farmer_profiles` | Verified |
| **Open Jobs** | Current open positions | `COUNT(*) FROM public.jobs WHERE status = 'open'` | Verified |
| **Filled / Completed Jobs**| Jobs successfully contracted | `COUNT(*) FROM public.jobs WHERE status = 'filled'` | Verified |
| **Active Produce Listings**| Current market listings | `COUNT(*) FROM public.produce_listings WHERE status = 'active'` | Verified |
| **Completed Orders** | Successfully delivered orders | `COUNT(*) FROM public.orders WHERE status = 'delivered'` | Verified |
| **Gross Marketplace Volume**| Real GMV in ₹ (excluding demo)| `COALESCE(SUM(total_paise), 0) / 100.0 FROM public.orders WHERE status = 'delivered'` | Verified |
| **Pending Verifications** | Verifications awaiting review | `COUNT(*) FROM public.kyc_records WHERE status = 'pending'` | Verified |
| **Open Complaints** | Unresolved dispute cases | `COUNT(*) FROM public.disputes WHERE status IN ('open', 'under_review', 'escalated')` | Verified |
| **Registrations Trend Chart**| Daily user signups | `DATE_TRUNC('day', created_at), COUNT(*) FROM public.profiles GROUP BY 1` | Verified |
| **Recent Activity Feed** | Real-time audit events | `SELECT * FROM admin.audit_logs ORDER BY created_at DESC LIMIT 10` | Verified |

---

## Module 2: User Management
| UI Component | Field / Column | Database Column / Type | Editable / Actionable |
| :--- | :--- | :--- | :--- |
| **User Grid** | User ID | `public.profiles.id` (UUID) | Read-only |
| | Full Name | `public.profiles.full_name` (TEXT) | Read-only |
| | Phone Number | `public.profiles.phone` (TEXT) | Read-only |
| | Active Role | `public.profiles.active_role` (`public.app_role`) | Filterable |
| | Language Preference | `public.profiles.language` (`public.lang_code`) | Read-only |
| | Account Status | `public.profiles.is_suspended` (BOOLEAN) | Toggleable with audit reason |
| | Created At | `public.profiles.created_at` (TIMESTAMPTZ) | Sortable |
| **Profile Drawer** | Primary Address | `public.addresses.address_text` (TEXT) | Read-only |
| | Default Landmark | `public.addresses.landmark` (TEXT) | Read-only |
| | Earnings Goal | `public.profiles.daily_earnings_goal_paise` (BIGINT) | Read-only |
| | Avatar Thumbnail | `public.profiles.avatar_path` (Signed Supabase URL) | Read-only |
| **Actions** | Suspend User | `UPDATE public.profiles SET is_suspended = true` | Requires reason dialog & audit log |
| | Restore User | `UPDATE public.profiles SET is_suspended = false` | Requires reason dialog & audit log |

---

## Module 3: Worker Management
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Worker Grid** | Worker Name | `public.profiles.full_name` via `user_id` | Verified |
| | Seeker Type | `public.worker_profiles.seeker_type` (`seeker_type`) | Verified |
| | Hourly Rate (₹) | `public.worker_profiles.hourly_rate_paise / 100.0` | Verified |
| | Daily Rate (₹) | `public.worker_profiles.daily_rate_paise / 100.0` | Verified |
| | Driving Licence | `public.worker_profiles.has_driving_licence` (BOOLEAN) | Verified |
| | Average Rating | `public.worker_profiles.rating_avg` (NUMERIC) | Verified (Read-only, guarded) |
| | Jobs Completed | `public.worker_profiles.jobs_completed_count` (INT) | Verified (Read-only, guarded) |
| | Verified Badge | `public.worker_profiles.is_verified_badge` (BOOLEAN) | Verified (Guarded by DB trigger) |
| **Worker Drawer**| Bio / Summary | `public.worker_profiles.bio` (TEXT) | Verified |
| | Working Days | `public.worker_profiles.working_days` (TEXT[]) | Verified |

---

## Module 4: Service Providers
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Provider Grid** | Provider Name | `public.profiles.full_name` via `user_id` | Verified |
| | Provider Type | `public.provider_profiles.provider_type` (`provider_type`) | Verified |
| | Typical Crew Size | `public.provider_profiles.typical_crew_size` (INT) | Verified |
| | Verified Badge | `public.provider_profiles.is_verified_badge` (BOOLEAN) | Verified |
| | Active Postings | `COUNT(j.id) FROM public.jobs j WHERE j.provider_id = user_id AND j.status = 'open'` | Verified |

---

## Module 5: Job Management
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Job Grid** | Job Title | `public.jobs.title` (TEXT) | Verified |
| | Provider | `public.profiles.full_name` via `provider_id` | Verified |
| | Wage Type | `public.jobs.wage_type` (`wage_type`) | Verified |
| | Wage Amount (₹) | `public.jobs.wage_paise / 100.0` | Verified |
| | Filled / Total Slots| `public.jobs.slots_filled || ' / ' || public.jobs.slots_total` | Verified |
| | Scheduled Start | `public.jobs.start_at` (TIMESTAMPTZ) | Verified |
| | Geofence Radius | `public.jobs.geofence_radius_m` (INT, default 150) | Verified |
| | Status | `public.jobs.status` (`job_status`) | Verified |
| **Detail Drawer**| Suitable For | `public.jobs.suitable_for` (`seeker_type[]`) | Verified |
| | Job Applications | List from `public.job_applications` (Worker Name, Note, Status, Applied At) | Verified |
| | Active Assignments| List from `public.job_assignments` (Worker, Booking Code, Shift Status, Wage) | Verified |
| **Actions** | Close / Cancel Job | `UPDATE public.jobs SET status = 'cancelled'` | Transactional with audit log |

---

## Module 6: Farmer Management
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Farmer Grid** | Farmer Name | `public.profiles.full_name` via `user_id` | Verified |
| | Farm Name | `public.farmer_profiles.farm_name` (TEXT) | Verified |
| | Agricultural Cluster| `public.farmer_profiles.cluster_name` (TEXT) | Verified |
| | Total Land (Acres) | `public.farmer_profiles.total_land_acres` (NUMERIC) | Verified |
| | Primary Crops | `public.farmer_profiles.primary_crops` (TEXT[]) | Verified |
| | Delivery Radius (km)| `public.farmer_profiles.delivery_radius_km` (NUMERIC) | Verified |
| | Organic Cert Number| `public.farmer_profiles.organic_cert_number` (TEXT) | Verified |

---

## Module 7: Produce Marketplace
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Listings Grid** | Crop Name | `public.produce_listings.crop_name` (TEXT) | Verified |
| | Category | `public.produce_listings.category` (`produce_category`) | Verified |
| | Price per Unit | `public.produce_listings.price_per_unit_paise / 100.0` | Verified |
| | Unit of Measure | `public.produce_listings.unit` (e.g. 'kg', 'crate') | Verified |
| | In Stock / Reserved | `public.produce_listings.stock_qty || ' / ' || reserved_qty` | Verified |
| | Status | `public.produce_listings.status` (`produce_status`) | Verified |
| **Lot Modal** | Lot Number & Token | `public.produce_lots.lot_number`, `lot_token` | Verified |
| | Agronomist Notes | `public.produce_lots.agronomist_notes` (TEXT) | Verified |

---

## Module 8: Orders Management
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Orders Grid** | Order Code | `public.orders.order_code` (TEXT) | Verified |
| | Customer | `public.profiles.full_name` via `customer_id` | Verified |
| | Farmer / Vendor | `public.farmer_profiles.farm_name` via `farmer_id` | Verified |
| | Total Amount (₹) | `public.orders.total_paise / 100.0` | Verified |
| | Platform Fee (₹) | `public.orders.platform_fee_paise / 100.0` | Verified |
| | Order Status | `public.orders.status` (`order_status`) | Verified |
| | Handover PIN | `public.orders.handover_pin` (4-digit string) | Verified |
| **Order Drawer** | Line Items | `public.order_items` (`quantity`, `unit_price_paise`) | Verified |
| | Fulfillment Timeline| `public.order_events` (`status_from`, `status_to`, `created_at`) | Verified |

---

## Module 9: Payments & Financial Transactions
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Gateway Payments**| Payment ID | `public.payments.id` (UUID) | Verified |
| | Payer | `public.profiles.full_name` via `payer_id` | Verified |
| | Amount (₹) | `public.payments.amount_paise / 100.0` | Verified |
| | Gateway Order ID | `public.payments.razorpay_order_id` (TEXT) | Verified |
| | Receipt Number | `public.payments.receipt_no` (TEXT) | Verified |
| | Status | `public.payments.status` (`payment_status`) | Verified |
| **Ledger Journal** | Journal Entry ID | `public.ledger_entries.id` (UUID) | Verified |
| | Direction / Bucket | `public.ledger_entries.direction`, `bucket` | Verified |
| | Amount (₹) | `public.ledger_entries.amount_paise / 100.0` | Verified |
| | Entry Type | `public.ledger_entries.entry_type` | Verified |
| | Balance After (₹) | `public.ledger_entries.balance_after_paise / 100.0` | Verified |
| **Demo Mode Isolation**| Demo Badge | Stored in `admin.demo_transactions` with striped amber badge | Verified (Zero real total impact) |

---

## Module 10: Identity Verification
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **KYC Records** | User Name | `public.profiles.full_name` via `user_id` | Verified |
| | Document Type | `public.kyc_records.doc_type` (`doc_type`) | Verified |
| | Masked Number | `public.kyc_records.doc_number_masked` (TEXT) | Verified |
| | Verification Status| `public.kyc_records.status` (`verification_status`)| Verified |
| | Document Photo | Signed, short-lived URL via Supabase Storage | Verified |
| **Biometric Safety**| Face Reference Photo| Signed URL via `public.face_profiles.reference_photo_path` | Verified (Embeddings never sent to client) |

---

## Module 11: Reviews & Ratings
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Reviews Grid** | Reviewer | `public.profiles.full_name` via `reviewer_id` | Verified |
| | Reviewee | `public.profiles.full_name` via `reviewee_id` | Verified |
| | Star Rating (1-5) | `public.ratings_reviews.rating` (INT) | Verified |
| | Tags & Comment | `public.ratings_reviews.tags`, `comment` | Verified |
| | Created At | `public.ratings_reviews.created_at` (TIMESTAMPTZ) | Verified |

---

## Module 12: Complaints & Disputes
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Disputes Grid** | Dispute ID | `public.disputes.id` (UUID) | Verified |
| | Complainant | `public.profiles.full_name` via `complainant_id` | Verified |
| | Respondent | `public.profiles.full_name` via `respondent_id` | Verified |
| | Category / Reason | `public.disputes.category`, `reason` | Verified |
| | Status | `public.disputes.status` (`open`, `under_review`, `escalated`, `resolved`) | Verified |
| **Evidence Drawer** | Evidence Photos | `public.dispute_evidence` (`file_path`, `description`) via signed URLs | Verified |

---

## Module 13: Voice Assistant Monitoring
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Request Counts** | Daily Calls | Aggregated from Edge Function call records | Verified |
| **Language Breakdown**| Language Code | `en`, `kn`, `hi`, `te`, `ta`, `mr`, `bn`, `or` | Verified |
| **Provider Routing** | AI Provider | Groq, Gemini, OpenRouter, Deepgram | Verified |
| **Raw Audio Policy** | Audio Recording | **NOT available in data model** (Displayed honestly via badge) | Verified |

---

## Module 14: Notifications
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Notification Logs**| Title & Body | `public.notifications.title`, `body` | Verified |
| | Target User | `public.profiles.full_name` via `user_id` | Verified |
| | Read Status | `public.notifications.is_read` (BOOLEAN) | Verified |
| | Sent At | `public.notifications.created_at` (TIMESTAMPTZ) | Verified |

---

## Module 15: Reports & Analytics
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Financial Report** | Volume, Fees, Escrow | Aggregated from `public.payments` and `public.ledger_entries` | Verified |
| **Employment Report**| Shifts, Workers, Hours | Aggregated from `public.job_assignments` | Verified |
| **Exports** | CSV & PDF Export | Streaming server-side export | Verified |

---

## Module 16: Admin Management
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Admin Grid** | Email | `admin.admin_users.email` (TEXT) | Verified |
| | Full Name | `admin.admin_users.full_name` (TEXT) | Verified |
| | Role | `admin.roles.name` (`SUPER_ADMIN`, `ADMIN`, `MODERATOR`, `SUPPORT`) | Verified |
| | Status | `admin.admin_users.is_active` (BOOLEAN) | Verified |

---

## Module 17: Audit Logs
| UI Component | Field / Column | Database Column / Type | Verification Status |
| :--- | :--- | :--- | :--- |
| **Audit Grid** | Timestamp | `admin.audit_logs.created_at` (TIMESTAMPTZ) | Verified |
| | Admin User | `admin.audit_logs.admin_email` (TEXT) | Verified |
| | Action Code | `admin.audit_logs.action` (TEXT) | Verified |
| | Target Entity | `admin.audit_logs.target_type`, `target_id` | Verified |
| | State Diff / Reason | `admin.audit_logs.diff_json` (JSONB), `reason` | Verified |

---

## Module 18: Settings
| UI Component | Field / Column | Source | Verification Status |
| :--- | :--- | :--- | :--- |
| **Profile** | Admin Email, Password | Supabase Auth API | Verified |
| **Preferences** | Theme & Motion Mode | Client LocalStorage + Admin Preference | Verified |
| **System Health** | DB & Actuator Status | `/actuator/health` endpoint | Verified |
