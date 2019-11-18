package com.truevalue.dreamappeal.bean

data class BeanBlueprint(var comment_count : Int,
                         var user_image : String,
                         var ability_and_opportunity : ArrayList<BeanBlueprintAnO>,
                         var objects : ArrayList<BeanBlueprintObject>)