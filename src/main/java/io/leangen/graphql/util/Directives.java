package io.leangen.graphql.util;

import graphql.DirectivesUtil;
import graphql.introspection.Introspection;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLDirectiveContainer;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;
import io.leangen.graphql.metadata.Operation;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;

public class Directives {

    private static final String MAPPED_TYPE = "_mappedType";
    private static final String MAPPED_OPERATION = "_mappedOperation";
    private static final String TYPE = "type";
    private static final String OPERATION = "operation";

    public static GraphQLDirective mappedType(AnnotatedType type) {
        return GraphQLDirective.newDirective()
                .name(MAPPED_TYPE)
                .description("")
                .validLocation(Introspection.DirectiveLocation.OBJECT)
                .argument(GraphQLArgument.newArgument()
                        .name(TYPE)
                        .description("")
                        .value(type)
                        .type(UNREPRESENTABLE)
                        .build())
                .build();
    }

    public static GraphQLDirective mappedOperation(Operation operation) {
        return GraphQLDirective.newDirective()
                .name(MAPPED_OPERATION)
                .description("")
                .validLocation(Introspection.DirectiveLocation.FIELD_DEFINITION)
                .argument(GraphQLArgument.newArgument()
                        .name(OPERATION)
                        .description("")
                        .value(operation)
                        .type(UNREPRESENTABLE)
                        .build())
                .build();
    }

    public static boolean isMappedType(GraphQLType type) {
        return type instanceof GraphQLDirectiveContainer && DirectivesUtil.directiveWithArg(((GraphQLDirectiveContainer) type).getDirectives(), MAPPED_TYPE, TYPE).isPresent();
    }

    public static AnnotatedType getMappedType(GraphQLType type) {
        return DirectivesUtil.directiveWithArg(((GraphQLDirectiveContainer) type).getDirectives(), MAPPED_TYPE, TYPE)
                .map(arg -> (AnnotatedType) arg.getValue())
                .orElseThrow(() -> new IllegalArgumentException("GraphQL type " + type.getName() + " does not have a mapped Java type"));
    }

    public static Optional<Operation> getMappedOperation(GraphQLFieldDefinition field) {
        return DirectivesUtil.directiveWithArg(field.getDirectives(), MAPPED_OPERATION, OPERATION)
                .map(arg -> (Operation) arg.getValue());
    }

    private static final GraphQLScalarType UNREPRESENTABLE = new GraphQLScalarType("UNREPRESENTABLE", "Unrepresentable type", new Coercing() {
        private static final String ERROR = "Type not intended for use";

        @Override
        public Object serialize(Object dataFetcherResult) {
            throw new CoercingSerializeException(ERROR);
        }

        @Override
        public Object parseValue(Object input) {
            throw new CoercingParseValueException(ERROR);
        }

        @Override
        public Object parseLiteral(Object input) {
            throw new CoercingParseLiteralException(ERROR);
        }
    });
}
