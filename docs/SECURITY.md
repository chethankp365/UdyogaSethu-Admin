# UdyogaSethu Admin — Security Architecture & RBAC Matrix

## 1. Authentication Architecture

- **Supabase Auth Gateway**: Validates JWT signatures and expiry server-side against Supabase JWKS.
- **Trusted Admin Table Check**: Even with a valid Supabase user token, authentication is **strictly denied** unless the user exists in `admin.admin_users` and has `is_active = true`.
- **Session Security**: HttpOnly, SameSite=Strict cookies with idle session timeout.
- **CSRF Protection**: Native Spring Security and Vaadin token validation active on all mutations.

---

## 2. RBAC Authority Tiers & Permission Matrix

| Module / Sensitive Action | SUPER_ADMIN | ADMIN | MODERATOR | SUPPORT |
| :--- | :---: | :---: | :---: | :---: |
| **View Dashboard & Analytics** | ✅ | ✅ | ✅ | ✅ |
| **Inspect User / Worker / Provider** | ✅ | ✅ | ✅ | ✅ |
| **Suspend / Restore User Account** | ✅ | ✅ | ❌ | ❌ |
| **Cancel Job Posting** | ✅ | ✅ | ✅ | ❌ |
| **Approve / Reject KYC Document** | ✅ | ✅ | ✅ | ❌ |
| **Resolve Grievance Dispute** | ✅ | ✅ | ✅ | ❌ |
| **Manage Sandbox Demo Transactions** | ✅ | ❌ | ❌ | ❌ |
| **Provision / Deactivate Admin** | ✅ | ❌ | ❌ | ❌ |
| **View Security Audit Logs** | ✅ | ✅ | ❌ | ❌ |
| **Deactivate Last Super Admin** | ❌ (Blocked) | ❌ (Blocked) | ❌ (Blocked) | ❌ (Blocked) |

---

## 3. Data Protection & Privacy Guardrails

1. **Biometric Safeguard**: Raw face embeddings (`public.face_profiles.embedding`) are never fetched or displayed in any UI view.
2. **Signed Storage URLs**: Document photos and KYC records are accessed only via short-lived, signed Supabase storage URLs.
3. **Append-Only Audit Logs**: Trigger `admin.trg_prevent_audit_mutation` guarantees audit rows cannot be edited or deleted by any application role.
4. **Secret Isolation**: No database passwords, JWT secrets, or payment credentials exist in source code. All configuration is injected via environment variables.
