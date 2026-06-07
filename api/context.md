I’m checking the authentication wiring so I can tell whether the anonymous token is coming from missing Spring Security config or from the token filter not populating the context.

Searched for regex `SecurityFilterChain|WebSecurityConfigurerAdapter|AnonymousAuthenticationToken|SecurityContextHolder|OncePerRequestFilter|Bearer` (`**/src/main/java/**/*.java`), 5 results

I found a `SecurityConfig`; I’m opening it now to confirm whether `/users/me` is protected and whether the app is actually setting an authenticated principal.

Read [](file:///c%3A/ecom-fullstack/api/src/main/java/com/vu/api/config/SecurityConfig.java)

I confirmed the security rule: `/users/**` is `permitAll`, so Spring is allowed to leave the request anonymous. I’m checking whether there’s any JWT/auth filter that could still populate the context before I recommend the minimal fix.

Searched for regex `Jwt|JWT|Authorization|Bearer|OncePerRequestFilter|UsernamePasswordAuthenticationToken|UserDetailsService` (`**/src/main/java/**/*.java`), 19 results

I found the bigger issue: the project generates JWTs, but I haven’t seen any filter that reads `Authorization: Bearer ...` and sets `Authentication`. I’m opening the JWT service so I can wire the same parser into a filter instead of guessing.

Read [](file:///c%3A/ecom-fullstack/api/src/main/java/com/vu/api/service/Impl/AuthenticationServiceImpl.java)

I’ve confirmed the app issues JWTs with Nimbus, but there’s no authentication filter yet. I’m going to add one, then tighten `/users/me` so it can’t fall through as anonymous.

Generating patch (92 lines) in [](file:///c%3A/ecom-fullstack/api/src/main/java/com/vu/api/config/JwtAuthenticationFilter.java)


```

```


Generating patch (47 lines) in [](file:///c%3A/ecom-fullstack/api/src/main/java/com/vu/api/config/SecurityConfig.java)


```

```


Generating patch (34 lines) in [](file:///c%3A/ecom-fullstack/api/src/main/java/com/vu/api/service/Impl/UserServiceImpl.java)


```

```
The auth path is now wired end-to-end; I’m compiling the project next to catch any Nimbus/Spring Security integration issues before I stop.

Ran terminal command: ./mvnw -q -DskipTests compile

The compile step returned cleanly with no output, so I’m doing one narrow error check on the touched files to make sure there isn’t a hidden import or API mismatch.

Checked [](file:///c%3A/ecom-fullstack/api/src/main/java/com/vu/api/config/JwtAuthenticationFilter.java), [](file:///c%3A/ecom-fullstack/api/src/main/java/com/vu/api/config/SecurityConfig.java), [](file:///c%3A/ecom-fullstack/api/src/main/java/com/vu/api/service/Impl/UserServiceImpl.java), no problems found

Nguyên nhân là request của bạn đang đi vào Spring Security dưới trạng thái anonymous, nên `SecurityContextHolder` chứa `AnonymousAuthenticationToken` với `principal = anonymousUser`. Trong project này, `SecurityConfig` đang để `/users/**` là `permitAll`, và trước đó chưa có filter nào đọc `Authorization: Bearer ...` để gán `Authentication` thật vào context.

Tôi đã nối lại đúng luồng này:

- Thêm filter JWT để đọc header Bearer, verify token bằng `jwt.signerKey`, rồi set `Authentication` vào `SecurityContextHolder` trong JwtAuthenticationFilter.java.
- Đăng ký filter đó và yêu cầu riêng `/users/me` phải authenticated trong SecurityConfig.java.
- Chặn anonymous auth rõ ràng trong UserServiceImpl.java, để `/me` không còn lấy nhầm `anonymousUser`.

Tôi cũng đã compile kiểm tra, và 3 file này không báo lỗi.

Nếu sau khi sửa mà vẫn thấy `anonymousUser`, thì gần như chắc chắn là một trong các điểm sau:
- Header không đúng dạng `Authorization: Bearer <token>`
- Token hết hạn
- `jwt.signerKey` trên server không khớp với key đã dùng để ký token
- Token không phải JWT do hệ thống này phát ra

Nếu bạn muốn, tôi có thể tiếp tục kiểm tra luôn `AuthenticationController` để xác nhận token login trả ra đúng format và test một request `/users/me` mẫu.

Made changes.