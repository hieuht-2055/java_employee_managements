package com.example.employeemanagement.auth.jwt;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.employeemanagement.auth.dto.TokenResponse;

// Lab 9: tạo access token cho user đã xác thực.
// Payload: {"iss": "...", "sub": "admin", "roles": ["ADMIN"], "iat": ..., "exp": ...}
// JWT chỉ KÝ, không mã hoá → ai cũng đọc được payload (base64), không đặt dữ liệu nhạy cảm vào đây.
@Service
public class JwtTokenService {

    public static final String ROLES_CLAIM = "roles";

    private final JwtEncoder encoder;
    private final JwtProperties properties;

    public JwtTokenService(JwtEncoder encoder, JwtProperties properties) {
        this.encoder = encoder;
        this.properties = properties;
    }

    public TokenResponse issue(Authentication authentication) {
        Instant now = Instant.now();
        // "ROLE_ADMIN" → "ADMIN"; khi đọc lại, JwtAuthenticationConverter thêm ROLE_ trở lại.
        // Chỉ lấy authority ROLE_*: Security 7 (MFA) tự thêm "FACTOR_PASSWORD" sau khi đăng nhập bằng mật khẩu,
        // không lọc thì token sẽ mang thêm quyền giả ROLE_FACTOR_PASSWORD.
        List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> a.startsWith("ROLE_"))
            .map(a -> a.substring("ROLE_".length()))
            .toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(properties.issuer())
            .subject(authentication.getName())
            .issuedAt(now)
            .expiresAt(now.plus(properties.ttl()))
            .claim(ROLES_CLAIM, roles)
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", properties.ttl().toSeconds(), authentication.getName(), roles);
    }
}
