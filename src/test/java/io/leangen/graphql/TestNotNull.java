package io.leangen.graphql;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.junit.Test;

import javax.validation.constraints.NotNull;

import static org.junit.Assert.assertTrue;

public class TestNotNull {

    @Test
    public void testJavaxValidateNotNull() {
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withOperationsFromSingleton(new Dummy())
                .generate();

        GraphQLObjectType objectType = schema.getObjectType("Cat");

        GraphQLFieldDefinition fieldDef = objectType.getFieldDefinition("id");
        GraphQLOutputType outputType = fieldDef.getType();
        System.out.println(outputType.toString());

        // if declared not null then it starts with GraphQLNonNull, otherwise with GraphQLScalarType
        assertTrue(outputType.toString().startsWith("GraphQLNonNull"));


    }

    public static class Dummy {
        @GraphQLQuery
        public Cat getCat() {
            Cat cat = new Cat();
            cat.setId("1");
            return cat;
        }
    }

    public static class Cat {
        private String id;
        private String name;

        @NotNull
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
