package kr.spot.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import kr.spot.auth.api.TokenPort;
import kr.spot.auth.api.dto.TokenDTO;
import kr.spot.code.status.ErrorStatus;
import kr.spot.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenPort {

    public static final String MEMBER_ID = "memberId";
    public static final String TOKEN_TYPE = "tokenType";
    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    @Value("${token.access_secret}")
    private String JWT_SECRET_KEY;
    @Value("${token.access_token_expiration_time}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;
    @Value("${token.refresh_token_expiration_time}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME;

    @PostConstruct
    protected void init() {
        JWT_SECRET_KEY = Base64.getEncoder().encodeToString(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TokenDTO createToken(Long memberId) {
        // 현재 시간
        Date now = new Date();
        String accessToken = generateToken(memberId, now, ACCESS_TOKEN_EXPIRATION_TIME, ACCESS); // 액세스 토큰 생성
        String refreshToken = generateToken(memberId, now, REFRESH_TOKEN_EXPIRATION_TIME, REFRESH); // 리프레시 토큰 생성

        // 토큰 DTO 반환
        return TokenDTO.of(accessToken, refreshToken);
    }

    private String generateToken(Long memberId, Date now, long expirationTime, String tokenType) {
        return Jwts.builder()
                .claim(MEMBER_ID, memberId) // 회원 ID
                .claim(TOKEN_TYPE, tokenType) // 토큰 타입
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + expirationTime)) // 만료 시간
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token);
        } catch (SecurityException | MalformedJwtException e) {
            throw new GeneralException(ErrorStatus._INVALID_JWT);
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorStatus._EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new GeneralException(ErrorStatus._UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus._EMPTY_JWT);
        }
    }

    @Override
    public Long getMemberIdByToken(String token) {
        Claims claims = getClaims(token);
        return claims.get(MEMBER_ID, Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
