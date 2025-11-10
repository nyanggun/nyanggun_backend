package org.kosa.congmouse.nyanggoon.config;


import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.kosa.congmouse.nyanggoon.exception.GlobalErrorResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Tag(name = "한국문화사냥꾼", description = "한국문화사냥꾼 API")
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){

        // 401 응답
        ApiResponse unauthorized = new ApiResponse()
                .description("인증을 실패하였습니다.")
                .content(new Content().addMediaType("application/json"
                        , new MediaType()
                                .schema(new Schema<>()
                                .$ref("#/components/schemas/GlobalErrorResponse"))
                                .example(Map.of(
                                        "timestamp", "2025-11-06T10:20:30",
                                        "status", 401,
                                        "code", "UNAUTHORIZED",
                                        "message", "로그인이 필요하거나 유효하지 않은 토큰입니다."
                                ))));

        // 404 응답
        ApiResponse badRequest = new ApiResponse()
                .description("사용자가 요청한 리소스를 찾을 수 없습니다.")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>()
                                .$ref("#/components/schemas/GlobalErrorResponse"))
                                .example(Map.of(
                                        "timestamp", "2025-11-06T10:20:30",
                                        "status", 404,
                                        "code", "NOT_FOUND",
                                        "message", "요청한 리소스를 찾을 수 없습니다."
                                ))));

        // 500 응답
        ApiResponse serverError = new ApiResponse()
                .description("예상하지 못한 오류가 발생했습니다.")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>()
                                .$ref("#/components/schemas/GlobalErrorResponse"))
                                .example(Map.of(
                                        "timestamp", "2025-11-06T10:20:30",
                                        "status", 500,
                                        "code", "INTERNAL_SERVER_ERROR",
                                        "message", "서버 내부 오류가 발생했습니다."
                                ))));

        // components 정의
        Components components = new Components()
                // 에러 스키마 등록
                .addSchemas("GlobalErrorResponse",
                        // reflection API 사용해 GlobalErrorResponse 변경에도 자동 반영되도록 등록
                        ModelConverters.getInstance()
                                .read(GlobalErrorResponse.class)
                                .get("GlobalErrorResponse")
                        .addProperty("timestamp", new StringSchema().example("2025-11-06T10:20:30"))
                        .addProperty("status", new IntegerSchema().example(500))
                        .addProperty("code", new StringSchema().example("INTERNAL_ERROR"))
                        .addProperty("message", new StringSchema().example("서버 내부 오류가 발생했습니다.")))
                // JWT 인증 스키마
                .addSecuritySchemes("JWT Token", new SecurityScheme()
                        .name("JWT Token")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSecuritySchemes("Admin JWT Token",
                        new SecurityScheme()
                                .name("Admin JWT Token")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("관리자 권한이 포함된 JWT 토큰"))
                // 공통 응답 등록
                .addResponses("Unauthorized", unauthorized)
                .addResponses("BadRequest", badRequest)
                .addResponses("InternalServerError", serverError);

        return new OpenAPI()
                .info(new Info()
                        .title("한국문화사냥꾼 API")
                        .description("한국문화사냥꾼 서비스 관리를 위한 REST API \n\n"
                         + "모든 응답은 아래의 **공통 Response 형식(ApiResponseDto)** 을 따릅니다.\n\n"
                         + "공통 Response 형식이 아닌 경우 exmaple value를 확인하시면 됩니다.\n" +
                                "                            \n" +
                                "                            \n" +
                                "                            공통 응답 구조\n" +
                                "                            Media type : application/json\n" +
                                "                            {\n" +
                                "                              \"success\": true,\n" +
                                "                              \"message\": \"요청이 성공적으로 처리되었습니다.\",\n" +
                                "                              \"data\": { ... },   // 요청별 응답 데이터\n" +
                                "                              \"code\": \"200\",\n" +
                                "                              \"timestamp\": \"2025-11-06T10:20:30\"\n" +
                                "                            }\n" +
                                "                            - **success** : 요청 성공 여부  \n" +
                                "                            - **message** : 처리 결과 메시지  \n" +
                                "                            - **data** : API별 응답 데이터 (객체, 리스트, null 등)  \n" +
                                "                            - **code** : 내부 응답 코드 (HTTP 코드와 무관할 수 있음)  \n" +
                                "                            - **timestamp** : 처리 시각 (ISO-8601 형식) \n ")
                        .version("1.0.0")
                        .contact(new Contact().name("순형이").email("glamf98@gmail.com")))
                .components(components);
    }

    // 전역 공통 응답 등록
    // 모든 API의 Response 목록에 공통 코드(401, 404, 500)를 자동으로 추가
    // 1. openApi.getPaths() → Swagger에 등록된 모든 엔드포인트(path)를 가져옴
    // 2. pathItem.readOperations() → 각 엔드포인트의 메서드(GET, POST 등)를 가져옴
    // 3. operation.getResponses() → 그 API의 응답 목록(200, 201 등)에 접근
    // 4. addApiResponse("404", …) → 각 API의 response 목록에 “404”를 추가함
    @Bean
    public OpenApiCustomizer globalResponseCustomiser(){
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    operation.getResponses()
                            .addApiResponse("404",
                                    new ApiResponse().$ref("#/components/responses/BadRequest"))
                            .addApiResponse("500",
                                    new ApiResponse().$ref("#/components/responses" +
                                            "/InternalServerError"))
                            .addApiResponse("401",
                                    new ApiResponse().$ref("#/components/responses/Unauthorized"));
                }));
    }
}
