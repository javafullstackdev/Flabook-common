package com.msquare.flabook;


import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
import com.msquare.flabook.enumeration.PostingType;
import com.msquare.flabook.form.CreatePostVo;
import com.msquare.flabook.service.PostingService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
/*
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WebMvcTest(FeedController.class)
@Import(WebSecurityConfig.class)*/
public class PostingControllerTests {

    private static final Logger log = LoggerFactory.getLogger(PostingControllerTests.class);

    /*@Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(); // (1)

     */
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private RestDocumentationResultHandler document;

    private MockMvc mockMvc;

    @MockBean
    private PostingService postingService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        this.document = document(
                "{class-name}/{method-name}",
                preprocessResponse(prettyPrint())
        );
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation)
                        .uris().withScheme("https").withHost("kingcjy.com").withPort(443))
                .alwaysDo(document)
                .build();
    }


    //private RestDocumentationResultHandler document;


 /*   // (3)
    @Before
    public void setUp() {

        log.info("setUp");
  *//*      this.document = document(
                "{class-name}/{method-name}",
                preprocessResponse(prettyPrint())
        );*//*
*//*
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(document)
                .build();*//*
    }*/


    //@Test
    //@WithMockUser(username = "user1", roles = {"USER"})
    public void 테스트() throws Exception {

        log.info("test {}", mockMvc);

        CreatePostVo vo = new CreatePostVo();
        vo.setPostingType(PostingType.question);
        vo.setText("text");

        ObjectMapper om = new ObjectMapper();



        document("v2/postings", responseFields(
                fieldWithPath("id").description("글의 고유 id"),
                fieldWithPath("contents.contents").description("글의 내용"),
                fieldWithPath("createdAt").description("글 작성 일자"),
                fieldWithPath("updatedAt").description("글 수정 일자"),
                fieldWithPath("totalComment").description("글에 달린 댓글의 총 갯수"))
        );



//        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(productDto);
//        Map<String, Object> data = new Gson().fromJson(jsonString, Map.class);

      /*  mockMvc.perform(
                post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(""))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("name").description("상품 이름"),
                                fieldWithPath("desc").description("상품 설명"),
                                fieldWithPath("quantity").type(Integer.class).description("상품 수량")
                        )
                ));
*/

       /* this.document = document(
                "{class-name}/{method-name}",
                preprocessResponse(prettyPrint())
        );
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation)
                        .uris().withScheme("https").withHost("kingcjy.com").withPort(443))
                .alwaysDo(document)
                .build();*/





        FileInputStream fis = new FileInputStream("/Users/user/Downloads/image002.png");

        MockMultipartFile upload = new MockMultipartFile("files[]", fis);
        MockMultipartFile text = new MockMultipartFile("descriptions[]", "",
                "text/plain", "hello".getBytes(StandardCharsets.UTF_8));


 /*       //RestDocumentationRequestBuilders
        //ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart("/v2/postings")
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/v2/postings")
                        .file(upload)
                        .file(upload)
//                .file(text)
//                .file(text)
                        //.file(text)
                        .param("descriptions", "hello1")
//                       .param("descriptions", "hello2")
                        .param("text", "hello")
                        .param("postingType", PostingType.question.toString())
                        //.content("text=hello&postingType=" + PostingType.question.toString())
                        .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNTg4MjExMzM1fQ.weIjoHkRq2zBNhuRkcynRDMbX44u72jKjJyJxHQYq0pRNS6mShCgD81n7a3JVtFVy5DZoAnGuXj_VC21FrfvbQ")
                        //.part(new MockPart("text", "hello".getBytes(StandardCharsets.UTF_8)))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        );


*/
/*

        log.info("response : {}", result.andReturn().getResponse().getContentAsString());

        result.andDo(document("질문 등록1", // (4)
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
*/
/*                        pathParameters(
//                            parameterWithName("id").description("아이디")
                        ),*//*

         */
/*                requestBody(

                ),*//*

                requestHeaders(
                        headerWithName("Authorization").description(
                                "인증 토큰"))
                ,

                requestParts(
                        partWithName("files").description("The file to upload"),
                        partWithName("files").description("The file to upload")
//                        partWithName("descriptions[]").description("The file to upload")
                ) ,

                requestParameters(
                    parameterWithName("text").description("내용"),
                    parameterWithName("descriptions").description("내용").optional(),
                    parameterWithName("descriptions").description("내용").optional(),
                    parameterWithName("postingType").description("내용").optional()
                ),

                */
/*requestFields(
                        fieldWithPath("postingType").description("타입"),
                        fieldWithPath("text").description("내용")
                ),*//*

                responseFields(
                        fieldWithPath("resultCode").description("결과코드")
                        //                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                        //                        fieldWithPath("data.person.id").type(JsonFieldType.NUMBER).description("아이디"),
                        //                        fieldWithPath("data.person.firstName").type(JsonFieldType.STRING).description("이름"),
                        //                        fieldWithPath("data.person.lastName").type(JsonFieldType.STRING).description("성"),
                        //                        fieldWithPath("data.person.age").type(JsonFieldType.NUMBER).description("나이"),
                        //                        fieldWithPath("data.person.birthDate").type(JsonFieldType.STRING).description("생년월일"),
                        //                        fieldWithPath("data.person.gender").type(JsonFieldType.STRING).description("성별"),
                        //                        fieldWithPath("data.person.hobby").type(JsonFieldType.STRING).description("취미")
                )
        ));
*/


        //.content(CreatePostVo.builder().postingType(PostingType.question).build())

        /*ResultActions result = this.mockMvc.perform(
                get("/postings/{id}", 1L)
                        .content(objectMapper.writeValueAsString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );*/


        if (true)
            return;


        String d2 = this.mockMvc
                .perform(
                        RestDocumentationRequestBuilders.get("/v2/feed")
                                .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNTg4MjExMzM1fQ.weIjoHkRq2zBNhuRkcynRDMbX44u72jKjJyJxHQYq0pRNS6mShCgD81n7a3JVtFVy5DZoAnGuXj_VC21FrfvbQ"))

                .andExpect(status().isOk())
                .andDo(document("질문 등록1", // (4)
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
/*                        pathParameters(
//                            parameterWithName("id").description("아이디")
                        ),*/
                        requestHeaders(
                                headerWithName("Authorization").description(
                                        "인증 토큰"))
                        ,
                        requestFields(
                                fieldWithPath("postingType").type(JsonFieldType.STRING).description("타입"),
                                fieldWithPath("text").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과코드")
                                //                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                //                        fieldWithPath("data.person.id").type(JsonFieldType.NUMBER).description("아이디"),
                                //                        fieldWithPath("data.person.firstName").type(JsonFieldType.STRING).description("이름"),
                                //                        fieldWithPath("data.person.lastName").type(JsonFieldType.STRING).description("성"),
                                //                        fieldWithPath("data.person.age").type(JsonFieldType.NUMBER).description("나이"),
                                //                        fieldWithPath("data.person.birthDate").type(JsonFieldType.STRING).description("생년월일"),
                                //                        fieldWithPath("data.person.gender").type(JsonFieldType.STRING).description("성별"),
                                //                        fieldWithPath("data.person.hobby").type(JsonFieldType.STRING).description("취미")
                        )
                )).andReturn().getResponse().getContentAsString();
        log.info("r : {}", d2);

/*

        String r = result.andExpect(status().isOk())
                .andDo(document("질문 등록", // (4)
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                    pathParameters(
//                            parameterWithName("id").description("아이디")
                    ),
                    requestFields(
//                            fieldWithPath("postingType").type(JsonFieldType.STRING).description("타입"),
//                            fieldWithPath("text").type(JsonFieldType.STRING).description("내용")
                    ),
                    responseFields(
                            fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과코드")
    //                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
    //                        fieldWithPath("data.person.id").type(JsonFieldType.NUMBER).description("아이디"),
    //                        fieldWithPath("data.person.firstName").type(JsonFieldType.STRING).description("이름"),
    //                        fieldWithPath("data.person.lastName").type(JsonFieldType.STRING).description("성"),
    //                        fieldWithPath("data.person.age").type(JsonFieldType.NUMBER).description("나이"),
    //                        fieldWithPath("data.person.birthDate").type(JsonFieldType.STRING).description("생년월일"),
    //                        fieldWithPath("data.person.gender").type(JsonFieldType.STRING).description("성별"),
    //                        fieldWithPath("data.person.hobby").type(JsonFieldType.STRING).description("취미")
                    )
        )).andReturn().getResponse().getContentAsString();
        log.info("r : {}", r);
*/


    }
}
