package com.example.babyfood.data.init

import com.example.babyfood.data.local.database.dao.RecipeDao
import com.example.babyfood.domain.model.Ingredient
import com.example.babyfood.domain.model.Nutrition
import com.example.babyfood.domain.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeInitializer @Inject constructor(
    private val recipeDao: RecipeDao
) {

    fun initializeBuiltInRecipes() {
        CoroutineScope(Dispatchers.IO).launch {
            // 检查是否已有内置食谱
            val existingBuiltInRecipes = recipeDao.getBuiltInRecipes().first()
            if (existingBuiltInRecipes.isNotEmpty()) {
                return@launch
            }

            // 预置内置食谱
            val builtInRecipes = getBuiltInRecipes()
            val builtInEntities = builtInRecipes.map { recipe ->
                com.example.babyfood.data.local.database.entity.RecipeEntity(
                    id = recipe.id,
                    name = recipe.name,
                    minAgeMonths = recipe.minAgeMonths,
                    maxAgeMonths = recipe.maxAgeMonths,
                    ingredients = recipe.ingredients,
                    steps = recipe.steps,
                    nutrition = recipe.nutrition,
                    category = recipe.category,
                    isBuiltIn = recipe.isBuiltIn,
                    imageUrl = recipe.imageUrl
                )
            }
            recipeDao.insertAll(builtInEntities)
        }
    }

    private fun getBuiltInRecipes(): List<Recipe> {
        return listOf(
            // 6-8个月 - 泥状
            Recipe(
                id = 0,
                name = "南瓜米糊",
                minAgeMonths = 6,
                maxAgeMonths = 8,
                ingredients = listOf(
                    Ingredient("南瓜", "50g", false),
                    Ingredient("婴儿米粉", "20g", false),
                    Ingredient("温开水", "适量", false)
                ),
                steps = listOf(
                    "南瓜去皮去籽，切成小块",
                    "将南瓜块蒸熟（约10-15分钟）",
                    "将蒸熟的南瓜压成泥状",
                    "用温开水冲泡婴儿米粉",
                    "将南瓜泥加入米糊中，搅拌均匀即可"
                ),
                nutrition = Nutrition(
                    calories = 85f,
                    protein = 1.5f,
                    fat = 0.3f,
                    carbohydrates = 18f,
                    fiber = 1.2f,
                    calcium = 15f,
                    iron = 0.5f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "胡萝卜土豆泥",
                minAgeMonths = 6,
                maxAgeMonths = 8,
                ingredients = listOf(
                    Ingredient("胡萝卜", "50g", false),
                    Ingredient("土豆", "50g", false)
                ),
                steps = listOf(
                    "胡萝卜和土豆去皮，切成小块",
                    "将胡萝卜和土豆分别蒸熟（约15分钟）",
                    "将蒸熟的胡萝卜和土豆分别压成泥状",
                    "将两种泥混合，搅拌均匀即可"
                ),
                nutrition = Nutrition(
                    calories = 65f,
                    protein = 1.2f,
                    fat = 0.2f,
                    carbohydrates = 14f,
                    fiber = 1.5f,
                    calcium = 12f,
                    iron = 0.4f
                ),
                category = "蔬菜",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "苹果泥",
                minAgeMonths = 6,
                maxAgeMonths = 8,
                ingredients = listOf(
                    Ingredient("苹果", "1个", false)
                ),
                steps = listOf(
                    "苹果去皮去核，切成小块",
                    "将苹果块蒸熟（约8-10分钟）或用微波炉加热",
                    "将蒸熟的苹果压成泥状即可"
                ),
                nutrition = Nutrition(
                    calories = 52f,
                    protein = 0.3f,
                    fat = 0.2f,
                    carbohydrates = 14f,
                    fiber = 2.4f,
                    calcium = 6f,
                    iron = 0.1f
                ),
                category = "水果",
                cookingTime = 10,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "香蕉泥",
                minAgeMonths = 6,
                maxAgeMonths = 8,
                ingredients = listOf(
                    Ingredient("熟透的香蕉", "半根", false)
                ),
                steps = listOf(
                    "选择熟透的香蕉（皮上有斑点）",
                    "去皮，用勺子压成泥状即可",
                    "可根据需要加入少量温开水调节稠度"
                ),
                nutrition = Nutrition(
                    calories = 89f,
                    protein = 1.1f,
                    fat = 0.3f,
                    carbohydrates = 23f,
                    fiber = 2.6f,
                    calcium = 5f,
                    iron = 0.3f
                ),
                category = "水果",
                cookingTime = 5,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "蛋黄泥",
                minAgeMonths = 7,
                maxAgeMonths = 8,
                ingredients = listOf(
                    Ingredient("鸡蛋黄", "1个", false),
                    Ingredient("温开水", "适量", false)
                ),
                steps = listOf(
                    "将鸡蛋煮熟（水开后煮8-10分钟）",
                    "取出蛋黄，去除蛋白",
                    "将蛋黄压碎",
                    "加入少量温开水，搅拌均匀成泥状"
                ),
                nutrition = Nutrition(
                    calories = 55f,
                    protein = 2.7f,
                    fat = 4.5f,
                    carbohydrates = 0.6f,
                    fiber = 0f,
                    calcium = 22f,
                    iron = 0.9f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),

            // 8-12个月 - 颗粒状
            Recipe(
                id = 0,
                name = "肉末粥",
                minAgeMonths = 8,
                maxAgeMonths = 12,
                ingredients = listOf(
                    Ingredient("瘦肉（猪里脊）", "30g", false),
                    Ingredient("大米", "30g", false),
                    Ingredient("水", "适量", false)
                ),
                steps = listOf(
                    "大米洗净，加水煮成粥",
                    "瘦肉剁成细末",
                    "将肉末加入粥中，继续煮10-15分钟",
                    "煮至肉末熟透，粥粘稠即可"
                ),
                nutrition = Nutrition(
                    calories = 120f,
                    protein = 8f,
                    fat = 3f,
                    carbohydrates = 15f,
                    fiber = 0.3f,
                    calcium = 10f,
                    iron = 1.5f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "西兰花土豆泥",
                minAgeMonths = 8,
                maxAgeMonths = 12,
                ingredients = listOf(
                    Ingredient("西兰花", "50g", false),
                    Ingredient("土豆", "50g", false)
                ),
                steps = listOf(
                    "西兰花洗净，掰成小朵",
                    "土豆去皮，切成小块",
                    "将西兰花和土豆分别蒸熟（约15分钟）",
                    "将蒸熟的西兰花和土豆压成泥状，保留少量颗粒",
                    "混合搅拌均匀即可"
                ),
                nutrition = Nutrition(
                    calories = 70f,
                    protein = 2.5f,
                    fat = 0.3f,
                    carbohydrates = 14f,
                    fiber = 2.5f,
                    calcium = 35f,
                    iron = 0.8f
                ),
                category = "蔬菜",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "鱼肉泥",
                minAgeMonths = 8,
                maxAgeMonths = 12,
                ingredients = listOf(
                    Ingredient("鳕鱼（或其他白肉鱼）", "50g", false),
                    Ingredient("姜", "1片", false)
                ),
                steps = listOf(
                    "鱼肉洗净，用姜片去腥",
                    "将鱼肉蒸熟（约8-10分钟）",
                    "去除鱼刺，将鱼肉压成泥状",
                    "可根据需要加入少量温水调节稠度"
                ),
                nutrition = Nutrition(
                    calories = 65f,
                    protein = 13f,
                    fat = 0.5f,
                    carbohydrates = 0f,
                    fiber = 0f,
                    calcium = 15f,
                    iron = 0.4f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "豆腐泥",
                minAgeMonths = 8,
                maxAgeMonths = 12,
                ingredients = listOf(
                    Ingredient("嫩豆腐", "50g", false)
                ),
                steps = listOf(
                    "将嫩豆腐切成小块",
                    "用开水焯一下（去除豆腥味）",
                    "将豆腐压成泥状即可",
                    "可加入少量蔬菜泥或水果泥调味"
                ),
                nutrition = Nutrition(
                    calories = 50f,
                    protein = 4f,
                    fat = 3f,
                    carbohydrates = 1.5f,
                    fiber = 0.5f,
                    calcium = 80f,
                    iron = 1.2f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),

            // 12-24个月 - 小块状
            Recipe(
                id = 0,
                name = "西红柿炒鸡蛋",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("西红柿", "1个", false),
                    Ingredient("鸡蛋", "1个", false),
                    Ingredient("植物油", "适量", false),
                    Ingredient("盐", "少许", false)
                ),
                steps = listOf(
                    "西红柿洗净，切成小块",
                    "鸡蛋打散",
                    "锅中放少许油，倒入蛋液炒熟，盛出",
                    "锅中再加少许油，放入西红柿炒出汁",
                    "加入炒好的鸡蛋，翻炒均匀即可"
                ),
                nutrition = Nutrition(
                    calories = 130f,
                    protein = 8f,
                    fat = 9f,
                    carbohydrates = 5f,
                    fiber = 1f,
                    calcium = 30f,
                    iron = 1.5f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "蔬菜肉末粥",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("大米", "40g", false),
                    Ingredient("瘦肉", "30g", false),
                    Ingredient("胡萝卜", "20g", false),
                    Ingredient("青菜", "20g", false)
                ),
                steps = listOf(
                    "大米洗净，加水煮成粥",
                    "瘦肉剁成细末",
                    "胡萝卜和青菜切成小丁",
                    "将肉末、胡萝卜丁加入粥中煮10分钟",
                    "最后加入青菜丁，煮2-3分钟即可"
                ),
                nutrition = Nutrition(
                    calories = 150f,
                    protein = 10f,
                    fat = 3f,
                    carbohydrates = 22f,
                    fiber = 1.5f,
                    calcium = 25f,
                    iron = 2.0f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "蒸蛋羹",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鸡蛋", "1个", false),
                    Ingredient("温水", "1.5倍蛋液量", false),
                    Ingredient("盐", "少许", false),
                    Ingredient("香油", "几滴", false)
                ),
                steps = listOf(
                    "鸡蛋打散",
                    "加入温水和少许盐，搅拌均匀",
                    "过筛去除泡沫",
                    "盖上保鲜膜，蒸锅水开后蒸8-10分钟",
                    "出锅后滴几滴香油即可"
                ),
                nutrition = Nutrition(
                    calories = 90f,
                    protein = 7f,
                    fat = 6f,
                    carbohydrates = 1f,
                    fiber = 0f,
                    calcium = 28f,
                    iron = 1.2f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "水果沙拉",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("苹果", "30g", false),
                    Ingredient("香蕉", "30g", false),
                    Ingredient("酸奶", "2勺", false)
                ),
                steps = listOf(
                    "苹果去皮，切成小丁",
                    "香蕉去皮，切成小丁",
                    "将水果丁混合",
                    "加入酸奶搅拌均匀即可"
                ),
                nutrition = Nutrition(
                    calories = 100f,
                    protein = 1.5f,
                    fat = 1f,
                    carbohydrates = 22f,
                    fiber = 2f,
                    calcium = 60f,
                    iron = 0.3f
                ),
                category = "水果",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "蔬菜小饼",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("面粉", "50g", false),
                    Ingredient("鸡蛋", "1个", false),
                    Ingredient("胡萝卜", "30g", false),
                    Ingredient("青菜", "30g", false),
                    Ingredient("植物油", "适量", false)
                ),
                steps = listOf(
                    "胡萝卜和青菜切碎",
                    "面粉中加入鸡蛋和适量水，调成面糊",
                    "加入蔬菜碎，搅拌均匀",
                    "平底锅刷少许油，舀入面糊，煎成小饼即可"
                ),
                nutrition = Nutrition(
                    calories = 180f,
                    protein = 8f,
                    fat = 6f,
                    carbohydrates = 25f,
                    fiber = 2f,
                    calcium = 40f,
                    iron = 2.5f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            // 12-24个月 - 午餐晚餐食谱
            Recipe(
                id = 0,
                name = "鸡肉粥",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鸡胸肉", "50g", false),
                    Ingredient("大米", "40g", false),
                    Ingredient("胡萝卜", "20g", false),
                    Ingredient("青菜", "20g", false)
                ),
                steps = listOf(
                    "鸡胸肉切成小丁，用少许姜腌制去腥",
                    "大米洗净煮成粥",
                    "胡萝卜和青菜切成小丁",
                    "将鸡肉丁和胡萝卜丁加入粥中煮10分钟",
                    "最后加入青菜丁，煮2-3分钟即可"
                ),
                nutrition = Nutrition(
                    calories = 200f,
                    protein = 15f,
                    fat = 4f,
                    carbohydrates = 25f,
                    fiber = 1.5f,
                    calcium = 20f,
                    iron = 2.0f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "牛肉土豆泥",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("牛肉（瘦）", "40g", false),
                    Ingredient("土豆", "80g", false),
                    Ingredient("洋葱", "10g", false)
                ),
                steps = listOf(
                    "牛肉剁成细末",
                    "土豆去皮，切成小块",
                    "洋葱切碎",
                    "土豆蒸熟压成泥",
                    "锅中放少许油，炒香洋葱和牛肉末",
                    "将炒好的牛肉末加入土豆泥中，搅拌均匀"
                ),
                nutrition = Nutrition(
                    calories = 180f,
                    protein = 12f,
                    fat = 5f,
                    carbohydrates = 22f,
                    fiber = 2f,
                    calcium = 15f,
                    iron = 2.5f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "清蒸鱼块",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鲈鱼（或其他少刺鱼）", "60g", false),
                    Ingredient("姜", "2片", false),
                    Ingredient("葱", "1根", false),
                    Ingredient("植物油", "少许", false)
                ),
                steps = listOf(
                    "鱼切成小块，用姜片腌制10分钟去腥",
                    "葱切成葱花",
                    "将鱼块放入蒸锅，大火蒸8-10分钟",
                    "出锅后淋上少许植物油，撒上葱花"
                ),
                nutrition = Nutrition(
                    calories = 100f,
                    protein = 18f,
                    fat = 2f,
                    carbohydrates = 0f,
                    fiber = 0f,
                    calcium = 25f,
                    iron = 0.8f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "虾仁炒蛋",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("虾仁", "40g", false),
                    Ingredient("鸡蛋", "1个", false),
                    Ingredient("植物油", "适量", false)
                ),
                steps = listOf(
                    "虾仁洗净，用少许盐腌制",
                    "鸡蛋打散",
                    "锅中放少许油，倒入虾仁炒熟，盛出",
                    "锅中再加少许油，倒入蛋液炒熟",
                    "加入虾仁，翻炒均匀即可"
                ),
                nutrition = Nutrition(
                    calories = 160f,
                    protein = 14f,
                    fat = 9f,
                    carbohydrates = 2f,
                    fiber = 0f,
                    calcium = 55f,
                    iron = 2.0f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "蔬菜肉丸汤",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("猪肉（瘦）", "50g", false),
                    Ingredient("胡萝卜", "20g", false),
                    Ingredient("青菜", "20g", false),
                    Ingredient("淀粉", "5g", false)
                ),
                steps = listOf(
                    "猪肉剁成肉末，加入少许淀粉和盐，搅拌均匀",
                    "胡萝卜和青菜切碎",
                    "锅中加水烧开",
                    "将肉末做成小丸子，放入锅中煮熟",
                    "加入胡萝卜和青菜，煮2-3分钟即可"
                ),
                nutrition = Nutrition(
                    calories = 150f,
                    protein = 12f,
                    fat = 6f,
                    carbohydrates = 10f,
                    fiber = 1f,
                    calcium = 18f,
                    iron = 2.2f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "番茄牛肉面",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("牛肉（瘦）", "40g", false),
                    Ingredient("儿童面条", "40g", false),
                    Ingredient("西红柿", "1个", false),
                    Ingredient("植物油", "适量", false)
                ),
                steps = listOf(
                    "牛肉切成小丁",
                    "西红柿去皮，切成小块",
                    "锅中放少许油，炒香牛肉丁",
                    "加入西红柿炒出汁",
                    "加水烧开，放入面条煮熟即可"
                ),
                nutrition = Nutrition(
                    calories = 220f,
                    protein = 12f,
                    fat = 5f,
                    carbohydrates = 30f,
                    fiber = 1.5f,
                    calcium = 22f,
                    iron = 2.5f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "蔬菜肉末蒸蛋",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鸡蛋", "1个", false),
                    Ingredient("猪肉（瘦）", "20g", false),
                    Ingredient("胡萝卜", "15g", false),
                    Ingredient("温水", "1.2倍蛋液量", false)
                ),
                steps = listOf(
                    "猪肉和胡萝卜剁成细末",
                    "鸡蛋打散，加入温水，搅拌均匀",
                    "过筛去除泡沫",
                    "加入肉末和胡萝卜末，搅拌均匀",
                    "盖上保鲜膜，蒸锅水开后蒸10-12分钟"
                ),
                nutrition = Nutrition(
                    calories = 140f,
                    protein = 10f,
                    fat = 8f,
                    carbohydrates = 3f,
                    fiber = 0.5f,
                    calcium = 35f,
                    iron = 2.0f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "土豆烧鸡块",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鸡胸肉", "50g", false),
                    Ingredient("土豆", "80g", false),
                    Ingredient("胡萝卜", "20g", false),
                    Ingredient("植物油", "适量", false)
                ),
                steps = listOf(
                    "鸡胸肉切成小块",
                    "土豆去皮，切成小块",
                    "胡萝卜切成小块",
                    "锅中放少许油，翻炒鸡块至变色",
                    "加入土豆和胡萝卜，加适量水",
                    "小火焖煮15-20分钟至软烂"
                ),
                nutrition = Nutrition(
                    calories = 210f,
                    protein = 14f,
                    fat = 5f,
                    carbohydrates = 28f,
                    fiber = 2f,
                    calcium = 18f,
                    iron = 2.0f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "三鲜小馄饨",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("猪肉（瘦）", "40g", false),
                    Ingredient("虾仁", "20g", false),
                    Ingredient("鸡蛋", "0.5个", false),
                    Ingredient("小馄饨皮", "10张", false)
                ),
                steps = listOf(
                    "猪肉和虾仁剁成细末",
                    "鸡蛋打散炒熟，切碎",
                    "将肉末、虾仁末、鸡蛋碎混合，加少许盐拌匀",
                    "取馄饨皮，包入馅料",
                    "锅中加水烧开，放入馄饨煮熟即可"
                ),
                nutrition = Nutrition(
                    calories = 180f,
                    protein = 12f,
                    fat = 6f,
                    carbohydrates = 22f,
                    fiber = 0.5f,
                    calcium = 30f,
                    iron = 2.0f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "蔬菜鸡肉丸",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鸡胸肉", "60g", false),
                    Ingredient("胡萝卜", "20g", false),
                    Ingredient("青菜", "20g", false),
                    Ingredient("淀粉", "5g", false)
                ),
                steps = listOf(
                    "鸡胸肉剁成肉泥",
                    "胡萝卜和青菜切碎",
                    "将鸡肉泥、蔬菜碎、淀粉混合，加少许盐搅拌均匀",
                    "做成小丸子",
                    "水开后放入丸子，煮熟即可"
                ),
                nutrition = Nutrition(
                    calories = 140f,
                    protein = 16f,
                    fat = 3f,
                    carbohydrates = 8f,
                    fiber = 1f,
                    calcium = 15f,
                    iron = 1.8f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "南瓜鸡肉粥",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鸡胸肉", "40g", false),
                    Ingredient("大米", "40g", false),
                    Ingredient("南瓜", "50g", false)
                ),
                steps = listOf(
                    "鸡胸肉切成小丁",
                    "大米洗净煮成粥",
                    "南瓜去皮，切成小块",
                    "将鸡肉丁和南瓜块加入粥中，煮至软烂"
                ),
                nutrition = Nutrition(
                    calories = 190f,
                    protein = 12f,
                    fat = 3f,
                    carbohydrates = 28f,
                    fiber = 1.5f,
                    calcium = 18f,
                    iron = 1.8f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "虾仁蒸蛋",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鸡蛋", "1个", false),
                    Ingredient("虾仁", "30g", false),
                    Ingredient("温水", "1.2倍蛋液量", false)
                ),
                steps = listOf(
                    "虾仁洗净，切成小丁",
                    "鸡蛋打散，加入温水，搅拌均匀",
                    "过筛去除泡沫",
                    "加入虾仁，搅拌均匀",
                    "盖上保鲜膜，蒸锅水开后蒸8-10分钟"
                ),
                nutrition = Nutrition(
                    calories = 130f,
                    protein = 11f,
                    fat = 7f,
                    carbohydrates = 2f,
                    fiber = 0f,
                    calcium = 45f,
                    iron = 1.5f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "蔬菜鸡蛋饼",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鸡蛋", "1个", false),
                    Ingredient("面粉", "30g", false),
                    Ingredient("胡萝卜", "20g", false),
                    Ingredient("青菜", "20g", false),
                    Ingredient("植物油", "适量", false)
                ),
                steps = listOf(
                    "胡萝卜和青菜切碎",
                    "鸡蛋打散，加入面粉和适量水调成面糊",
                    "加入蔬菜碎，搅拌均匀",
                    "平底锅刷少许油，倒入面糊，煎成小饼"
                ),
                nutrition = Nutrition(
                    calories = 160f,
                    protein = 7f,
                    fat = 6f,
                    carbohydrates = 20f,
                    fiber = 1.5f,
                    calcium = 35f,
                    iron = 2.0f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "红烧牛肉面",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("牛肉（瘦）", "50g", false),
                    Ingredient("儿童面条", "50g", false),
                    Ingredient("胡萝卜", "20g", false),
                    Ingredient("植物油", "适量", false)
                ),
                steps = listOf(
                    "牛肉切成小块",
                    "胡萝卜切成小块",
                    "锅中放少许油，翻炒牛肉块至变色",
                    "加入胡萝卜和适量水，小火焖煮20分钟",
                    "加入面条，煮熟即可"
                ),
                nutrition = Nutrition(
                    calories = 250f,
                    protein = 14f,
                    fat = 6f,
                    carbohydrates = 32f,
                    fiber = 1.5f,
                    calcium = 25f,
                    iron = 3.0f
                ),
                category = "主食",
                cookingTime = 15,
                isBuiltIn = true
            ),
            Recipe(
                id = 0,
                name = "鱼丸汤",
                minAgeMonths = 12,
                maxAgeMonths = 24,
                ingredients = listOf(
                    Ingredient("鲈鱼（或其他少刺鱼）", "60g", false),
                    Ingredient("淀粉", "5g", false),
                    Ingredient("青菜", "20g", false)
                ),
                steps = listOf(
                    "鱼肉去刺，剁成鱼泥",
                    "加入淀粉，搅拌均匀",
                    "做成小丸子",
                    "锅中加水烧开，放入鱼丸煮熟",
                    "加入青菜，煮1-2分钟即可"
                ),
                nutrition = Nutrition(
                    calories = 110f,
                    protein = 18f,
                    fat = 2f,
                    carbohydrates = 4f,
                    fiber = 0.5f,
                    calcium = 28f,
                    iron = 0.8f
                ),
                category = "蛋白质",
                cookingTime = 15,
                isBuiltIn = true
            )
        )
    }
}