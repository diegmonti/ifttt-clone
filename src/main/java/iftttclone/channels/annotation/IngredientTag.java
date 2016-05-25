package iftttclone.channels.annotation;

import java.lang.annotation.Repeatable;

@Repeatable(IngredientsTag.class)
public @interface IngredientTag {
	String name();

	String description();

	String example();
}
