package com.ovvium.services.util.jpa.util;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.PathBuilder;
import com.ovvium.services.util.common.domain.Direction;
import com.ovvium.services.util.common.domain.Filter;
import com.ovvium.services.util.common.domain.Order;
import com.ovvium.services.util.util.basic.Traverser;
import com.ovvium.services.util.util.container.Pair;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QuerydslUtils {

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static <T> EntityPathBase<T> getEntityPathBase(Class<T> clss) {
        val sb = new StringBuilder(clss.getName());
        val p = sb.lastIndexOf(".");
        sb.insert(p + 1, "Q");
        val rootClass = Class.forName(sb.toString());
        for (val field : rootClass.getFields()) {
            if (EntityPathBase.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers())) {
                return (EntityPathBase<T>) field.get(null);
            }
        }
        return null;
    }

    public static Predicate getPredicate(PathBuilder<?> builder, Collection<Filter> filters) {

        BooleanBuilder predicates = new BooleanBuilder();

        for (Filter filter : Traverser.of(filters)) {
            try {

                String property = filter.getField();
                Object value = filter.getValue();
                Predicate predicate = null;

                switch (filter.getCondition()) {
                    case BETWEEN:
                        val pair = (Pair<?, ?>) value;
                        predicate = builder.getComparable(property, Comparable.class) //
                                .between((Comparable<?>) pair.getFirst(), (Comparable<?>) pair.getSecond());
                        break;
                    case CONTAINS:
                        predicate = builder.getString(property).contains((String) value);
                        break;
                    case CONTAINS_IGNORE_CASE:
                        predicate = builder.getString(property).containsIgnoreCase((String) value);
                        break;
                    case EMPTY:
                        predicate = builder.getString(property).isEmpty();
                        break;
                    case EMPTY_LIST:
                        predicate = builder.getCollection(property, Collection.class).isEmpty();
                        break;
                    case ENDS_WITH:
                        predicate = builder.getString(property).endsWith((String) value);
                        break;
                    case EQUALS:
                        predicate = builder.get(property).eq(value);
                        break;
                    case GE:
                        predicate = builder.getComparable(property, Comparable.class).goe((Comparable<?>) value);
                        break;
                    case GT:
                        predicate = builder.getComparable(property, Comparable.class).gt((Comparable<?>) value);
                        break;
                    case IN:
                        if(value instanceof Collection<?>) {
                            predicate = builder.get(property).in((Collection<?>)value);
                        } else {
                            predicate = builder.get(property).in(value);
                        }
                        break;
                    case IS_NOT_NULL:
                        predicate = builder.get(property).isNotNull();
                        break;
                    case IS_NULL:
                        predicate = builder.get(property).isNull();
                        break;
                    case LE:
                        predicate = builder.getComparable(property, Comparable.class).loe((Comparable<?>) value);
                        break;
                    case LT:
                        predicate = builder.getComparable(property, Comparable.class).lt((Comparable<?>) value);
                        break;
                    case MEMBER_OF:
                        predicate = builder.getCollection(property, Object.class).contains(value);
                        break;
                    case NOT_EQUALS:
                        predicate = builder.get(property).ne(value);
                        break;
                    case NOT_IN:
                        predicate = builder.getCollection(property, Object.class).contains(value).not();
                        break;
                    case STARTS_WITH:
                        predicate = builder.getString(property).startsWith((String) value);
                        break;
                    default:
                        break;
                }

                predicates = filter.isNot() ? predicates.andNot(predicate) : predicates.and(predicate);

            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Exception adding filter " + filter, e);
            }
        }
        return predicates;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static OrderSpecifier<Comparable<?>>[] getOrders(PathBuilder<?> builder, List<Order> orders) {
        val array = new OrderSpecifier<?>[orders.size()];
        val t = Traverser.of(orders);
        for (Order order : t) {
            val path = builder.getComparable(order.getProperty(), Comparable.class);
            val spec = new OrderSpecifier(getOrder(order.getDirection()), path);
            array[t.getPosition()] = spec;
        }
        return (OrderSpecifier<Comparable<?>>[]) array;
    }

    public static com.mysema.query.types.Order getOrder(Direction direction) {
        return direction == Direction.DESC ? com.mysema.query.types.Order.DESC : com.mysema.query.types.Order.ASC;
    }
}
