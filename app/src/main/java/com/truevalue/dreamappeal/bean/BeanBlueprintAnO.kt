package com.truevalue.dreamappeal.bean

data class BeanBlueprintAnO(var profile_idx : Int,
                            var idx : Int,
                            var contents : String,
                            var view_type : Int){
    // view_type = 0 은 Ability 1 은 Opportunity
    companion object{
        val VIEW_TYPE_ABILITY = 0
        val VIEW_TYPE_OPPORTUNITY = 1
    }
}
