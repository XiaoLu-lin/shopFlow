# shopflow-multiplatform-frontend Specification

## Purpose

Define the architecture and compatibility contract for rebuilding `shopflow-h5-next` as a uni-app frontend that can serve both H5 and WeChat Mini Program while preserving the current ShopFlow wx API contract.

## Requirements

### Requirement: The new frontend must target both H5 and WeChat Mini Program from one primary codebase

`shopflow-h5-next` MUST be redesigned as a `uni-app` frontend whose primary implementation target includes both H5 and `mp-weixin`, instead of remaining a H5-only SPA.

#### Scenario: architecture baseline fixes the multiplatform targets

- **Given** the project enters the next frontend rebuild phase
- **When** the target architecture is defined
- **Then** the primary frontend codebase must explicitly target both H5 and WeChat Mini Program
- **And** the architecture must not treat Mini Program support as an optional afterthought

#### Scenario: old frontends remain available during migration

- **Given** the new multiplatform frontend has not completed acceptance
- **When** migration work is in progress
- **Then** the existing `shopflow-h5` and `shopflow-wx` implementations must remain available
- **And** rollout must not assume immediate cutover

### Requirement: The new frontend must use a multiplatform-compatible component system

The new frontend MUST use a component strategy centered on `uView Plus`, project-owned `sf-*` business components, and `uni-app` built-in components.

#### Scenario: Vant is no longer the primary component foundation

- **Given** the new multiplatform frontend is being designed
- **When** the component foundation is selected
- **Then** `Vant` must not remain the primary component library
- **And** the chosen component system must support both H5 and WeChat Mini Program

#### Scenario: business pages depend on project-owned business components

- **Given** product, order, address, or checkout pages are implemented
- **When** business-facing UI is composed
- **Then** the pages should primarily consume project-owned `sf-*` business components for domain-specific UI
- **And** the pages should not deeply couple business structure to raw third-party component APIs

### Requirement: The new frontend must preserve current ShopFlow compatibility contracts

The multiplatform frontend MUST continue to preserve the current ShopFlow compatibility contract for wx-side APIs and stored user state.

#### Scenario: tenant bootstrap remains mandatory

- **Given** a user opens the homepage without a stored tenant token
- **When** the frontend starts its initial data bootstrap
- **Then** it must perform the current `appid + tenant` bootstrap flow
- **And** it must persist the returned tenant token for later wx requests

#### Scenario: logged-in requests keep the current user and tenant headers

- **Given** a user session is already stored locally
- **When** the frontend sends a protected `/wx/*` request
- **Then** the request must carry `X-ShopFlow-User-Token`
- **And** the request must carry `X-ShopFlow-TenantId`

#### Scenario: legacy local session keys remain readable

- **Given** a legacy H5 login session already exists in local storage
- **When** the new multiplatform frontend initializes its session state
- **Then** it must continue to read the current legacy-compatible login keys
- **And** the user should not be forced to re-login only because the frontend stack changed

### Requirement: Protected navigation behavior must stay compatible across platforms

The new frontend MUST keep the protected-route redirect semantics that the current H5 flow relies on, while adapting navigation to uni-app.

#### Scenario: protected access redirects to login with redirect preserved

- **Given** an anonymous user attempts to enter a protected page
- **When** access control is evaluated
- **Then** the frontend must navigate the user to login
- **And** it must preserve the intended redirect target

#### Scenario: legacy H5 links remain mappable into the new navigation model

- **Given** a user opens a historical H5 hash link
- **When** the new H5 frontend receives the legacy path
- **Then** the frontend must map that legacy path into a valid uni-app page target
- **And** unsupported links must fail visibly instead of silently landing on an unrelated page

### Requirement: Platform-specific capabilities must be isolated behind adapters

The new frontend MUST isolate storage, navigation, payment, share, upload, and rich-text differences behind explicit platform adapters.

#### Scenario: payment behavior is not hard-coded in shared pages

- **Given** checkout and payment pages are implemented
- **When** a payment action is triggered
- **Then** shared business pages must call a platform payment adapter
- **And** the page layer must not hard-code H5-only or Mini Program-only payment behavior inline

#### Scenario: share behavior remains platform-aware

- **Given** a product or campaign page needs share behavior
- **When** share metadata is prepared
- **Then** the frontend must route the behavior through a share adapter
- **And** H5 and Mini Program share behavior must remain independently configurable
