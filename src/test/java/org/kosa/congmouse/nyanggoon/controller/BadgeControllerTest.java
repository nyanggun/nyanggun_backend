package org.kosa.congmouse.nyanggoon.controller;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(BadgeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("BadgeController Unit Test")
public class BadgeControllerTest {

}
