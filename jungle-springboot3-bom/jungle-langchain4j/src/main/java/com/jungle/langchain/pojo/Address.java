package com.jungle.langchain.pojo;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
@Description("an address") // you can add an optional description to help an LLM have a better understanding
public class Address {
    String street;
    Integer streetNumber;
    String city;
}