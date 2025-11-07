package org.kosa.congmouse.nyanggoon.config;


import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.kosa.congmouse.nyanggoon.exception.GlobalErrorResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Tag(name = "한국문화사냥꾼", description = "한국문화사냥꾼 API")
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){
        ApiResponse unauthorized = new ApiResponse()
                .description("인증을 실패하였습니다.")
                .content(new Content().addMediaType("application/json"
                        , new MediaType().schema(new Schema<GlobalErrorResponse>()
                                .$ref("#/components/schemas/GlobalErrorResponse"))));

        ApiResponse badRequest = new ApiResponse()
                .description("사용자가 요청한 리소스를 찾을 수 없습니다.")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<GlobalErrorResponse>()
                                .$ref("#/components/schemas/GlobalErrorResponse"))));

        ApiResponse serverError = new ApiResponse()
                .description("예상하지 못한 오류 발생했습니다.")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<GlobalErrorResponse>()
                                .$ref("#/components/schemas/GlobalErrorResponse"))));

        Components components = new Components()
                .addSchemas("GlobalErrorResponse", new ObjectSchema()
                        .addProperty("timestamp", new StringSchema().example("2025-11-06T10:20:30"))
                        .addProperty("status", new IntegerSchema().example(500))
                        .addProperty("code", new StringSchema().example("INTERNAL_ERROR"))
                        .addProperty("message", new StringSchema().example("서버 내부 오류가 발생했습니다.")))
                .addResponses("Unauthorized", unauthorized)
                .addResponses("BadRequest", badRequest)
                .addResponses("InternalServerError", serverError);

        return new OpenAPI()
                .info(new Info()
                        .title("한국문화사냥꾼 API")
                        .description("한국문화사냥꾼 서비스 관리를 위한 REST API")
                        .version("1.0.0")
                        .contact(new Contact().name("순형이").email("glamf98@gmail.com")))
                .components(components);
    }

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
