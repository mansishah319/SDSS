# Smart Decision Support System (SDSS)

## 🚓 Overview
The **Smart Decision Support System (SDSS)** is a modern, enterprise-grade platform developed for Dubai Police to streamline decision-making, challenge management, and collaborative problem-solving. SDSS empowers users to analyze, document, and share solutions for recurring issues, and leverages Delphi Xs to gather insights and drive continuous improvement.

Built with **Vue.js 3** and **Vuetify 3**, SDSS delivers a responsive, accessible, and visually appealing user experience. The system is designed for scalability, security, and ease of maintenance.

---

## ✨ Key Features
- **Challenge & Solution Management**: Track, analyze, and document challenges. Store and retrieve solutions for recurring issues.
- **Delphi X Engine**: Conduct multi-round surveys to collect ideas, opinions, and feedback from diverse stakeholders.
- **Role-Based Access Control (RBAC)**: Fine-grained permissions for Admins, Moderators, Delphi Moderators, and regular users.
- **User & Profile Management**: Secure authentication, profile editing, and user directory.
- **Interactive Dashboards**: Visualize statistics, survey results, and challenge progress with rich charts and reports.
- **Modern UI/UX**: Animated, responsive, and accessible interface with dark/light theme support.
- **Audit & Logging**: Track user actions and maintain a history of changes for accountability.

---

## 🏗️ System Architecture
- **Frontend**: Vue.js 3, Vuetify 3, TypeScript, Pinia (state management), Vue Router
- **Backend**: (Not included in this repo)
- **API Communication**: Axios with secure token-based authentication
- **Internationalization**: i18n support for English and Arabic

---

## 🌍 Supported Environments & Running the App

SDSS supports multiple environments for development, testing, and production. You can run the app using either **pnpm** (recommended) or **npm**.

### **Available Environments**
- **local**: For local development and debugging
- **dev**: Shared development server for team testing
- **stg**: Staging environment for pre-production validation
- **prod**: Production/live environment for end-users

### **How to Run in Each Environment**

#### **Using pnpm**
```bash
  # Local development (default)
  pnpm run dev
  
  # Development server
  pnpm run dev:dev
  
  # Staging server
  pnpm run dev:stg
  
  # Production server
  pnpm run dev:prod
```

#### **Using npm**
```bash
  # Local development (default)
  npm run dev
  
  # Development server
  npm run dev:dev
  
  # Staging server
  npm run dev:stg
  
  # Production server
  npm run dev:prod
```

### **Building for Production**
```bash
  # Build for a specific environment
  pnpm run build:prod   # or build:dev, build:stg, build:local
  # Or with npm
  npm run build:prod
```

---

## 🔐 Authentication & Authorization
- **Authentication**: JWT-based login, with tokens stored securely in localStorage.
- **Authorization**: Route guards enforce access based on user roles. Example:
  - `/admin` and `/challenge/list`: Only accessible to Admins, Commanders, or Moderators.
  - `/users`, `/users/registration`, `/users/update/:grpNumber`: Only accessible to Admins.
  - `/surveys/admin-reports`: Only accessible to Delphi Moderators.
  - Unauthorized access redirects to Access Denied screen.

---

## 🌐 System Languages (i18n)
- **Supported Languages**: English (`en`) and Arabic (`ar`)
- The UI can be switched between English and Arabic at any time.
- All user-facing text is managed via i18n (`src/locales/en.json`, `src/locales/ar.json`).
- To add or update translations, edit the relevant JSON files.

---

## 📁 Folder Structure (Key Directories)
```
src/
  assets/         # Images, fonts, and global styles
  components/     # Reusable Vue components
  constants/      # App-wide constants and enums
  dto/            # Data Transfer Objects (API models)
  router/         # Vue Router configuration
  services/       # API service modules
  store/          # Pinia stores (state management)
  utils/          # Utility functions (auth, formatting, etc.)
  views/          # Page-level Vue components (routed views)
  locales/        # i18n translation files
```

---

## 🚀 Getting Started

### Prerequisites
- Node.js (v20 or higher)
- pnpm (recommended) or npm

### Installation
```bash
  git clone <repository-url>
  cd sdss-ui
  pnpm install   # or npm install
```

---

## 🧑‍💻 Code Quality & Contribution
- **Formatting**: Run `pnpm run format` or `npm run format` before committing.
- **Linting**: Run `pnpm run lint` or `npm run lint` and fix all issues.
- **Testing**: Test your changes locally before pushing.
- **Commits**: Use clear, descriptive commit messages.
- **Pull Requests**: Submit PRs for all changes. Include a summary and testing instruction.

---

## 🛡️ Security
- All sensitive routes are protected by navigation guards.
- JWT tokens are validated on each route change.
- User roles are enforced both in the UI and at the route level.

---

## 📞 Support & Contact
For questions, support, or to report issues, contact the [Development Team](mailto:eng.mo.abdu@gmail.com).

---

## 📜 License
_This project is licensed under the MIT License._

---

## 👨‍💻 Developer Information

- **Name:** Mohammed Abdu
- **Role:** Team Lead Full Stack Java Developer
- **Email:** eng.mo.abdu@gmail.com
- **Mobile (UAE):** +971501414755
- **Mobile (EG):** +201110076777