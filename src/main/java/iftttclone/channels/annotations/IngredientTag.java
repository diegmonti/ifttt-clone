package iftttclone.channels.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(IngredientsTag.class)
public @interface IngredientTag {
	String name();

	String description();

	String example();
}
