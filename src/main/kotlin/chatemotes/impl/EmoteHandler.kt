package chatemotes.impl

import net.minecraft.util.ResourceLocation

object EmoteHandler {
    // A lot of the actual implementation is in the Mixins to make accessing private methods and fields easier.
    // This Object provides some utility functions which i just cba to code in java.

    /**
     * Gets the possible start of an emote name from the text. Does not include the starting ":".
     * @param text The text in the chat input field.
     * @param pos The cursor position.
     * @return Returns the substring of text left of the curse position and right of the closest ":" in the string.
     * If nothing was found returns an empty string "".
     */
    fun getpossibleEmoteStart(text: String, pos: Int): String {
        val beforeCursor = text.substring(0, pos)
        return beforeCursor.substringAfterLast(":", "")
    }

    /**
     * Returns an array containing all possible matches for emotes at position in text.
     */
    fun getEmoteCompletions(text: String, pos: Int) : Array<String> {
        val startStr = getpossibleEmoteStart(text, pos)
        if (startStr.isBlank()) return arrayOf()
        val matcingKeys = emoteMap.keys.filter {
            it.startsWith(startStr)
        }.map { ":$it:" }

        return matcingKeys.toTypedArray()
    }

    /**
     * Checks whether there is an emote right in front of pos in the given text.
     */
    fun isEmoteInfrontOfCursor(text: String, pos: Int): Boolean{
        val beforeCursor = text.substring(0, pos)
        return endsWithEmote(beforeCursor)
    }

    /**
     * Checks whether all elements in the list are possible emotes by checking whether they all start and end with ":".
     */
    fun isOnlyEmotes(list: List<String>): Boolean{
        return list.filter { it.startsWith(":") && it.endsWith(":") }.size == list.size
    }

    /**
     * Returns a list of all matches for the emote
     */
    fun emoteMatches(text: String?): List<String>{
        if (text == null) return emptyList()
        val matches = regex.findAll(text)
        return matches.map { it.groupValues[1].removeSurrounding(":").lowercase() }.toList()
    }

    /**
     * Returns a list containing all the text elements surrounding the emotes.
     */
    fun splitText(text: String) = text.split(regex)

    /**
     * Returns true if the given text starts with an emote.
     * False otherwise.
     */
    fun startsWithEmote(text: String) = regex.matchesAt(text,0)

    /**
     * Returns true if the given text ends with an emote.
     * False otherwise.
     */
    fun endsWithEmote(text: String):Boolean {
      val regex2 = Regex(".*?$regexString", RegexOption.IGNORE_CASE)
        return regex2.matches(text)
    }

    /**
     * Returns the resource location for the emote with the given name or null if no emote exists for that name.
     */
    fun getEmoteResource(key: String?): ResourceLocation? {
        return if (emoteMap.containsKey(key))
            getResourceLocation(emoteMap[key]!!)
        else
            null
    }

    private fun getResourceLocation(name: String) : ResourceLocation {
        return ResourceLocation("chatemotes", "emotes/$name.png")
    }

    private val emoteMap = mapOf(
        "peepohappy"   to "709262692208214018",
        "peepoawesome" to "940262879800741928",
        "peeposad"     to "740206546696536145",
        "peepodagger"  to "748590105694044221",
        "peepohug"     to "748933314899083404",
        "peepothink"   to "751797786000883842",
        "prayge"       to "906733161784967278",
        "peepoglass"   to "751797984831995996",
        "peepowave"    to "741654049664794644",
        "babie"        to "982363194788052992",
        "peepofat"     to "982363194674794566",
        "peepostrong"  to "751797939814400120",
        "peeposhy"     to "969304714850218034",
        "fatpeepo"     to "863137218486599720",
        "peeposhrug"   to "585600922944798743",
        "huggies"      to "990430991430602772",
        "peepopat"     to "747989338134020248",
        "peeposleepo"  to "751797816346673264",
        "peepolove"    to "719305986703228978",
        "peepogun"     to "780963325785669652",
        "peeposcream"  to "965730655780634674",
        "peepog"       to "925411628198014996",
        "peepobaba"    to "1000520055156658316",
        "peepon"       to "967102945185370202",
        "lickies"      to "1012046946829090837",
        "bigfella"     to "1001490970526883850",
        "gatosexo"     to "858112447978405888",
        "pensive"      to "838818766128152576",
        "jaw"          to "900110186969202728",
        "peepowo"      to "1005987434485534721",

        "eyes"	        	        to "4c5a77a89716352686f590a6f014770c",
        "skull"                     to "f64f47a895e537305b3463f9d30bc177",
        "pray"		                to "1904291ab1aa5d14b2adaaff23a578dd",
        "smiley_cat"	            to "ef2af7fab48463e72a3a7f0f8fb4fb4e",
        "pouting_cat"  	            to "551f9d76028c39299e0bc9bc20cd0e0d",
        "crying_cat_face"	        to "509c4f6bb9b71c5d9551357942e11f12",
        "scream_cat"       	        to "3068417ae7f1a7c5c2ba60ab1aa1fb62",
        "kissing_cat"     	        to "ddd2c1bf4d9e5afa87394a52f1832fe4",
        "smirk_cat"	                to "e25128510c26b0aad9d71bc6cf49df67",
        "hear_eyes_cat" 	        to "d4d91a5f31668dd4609a3d7522f722c5",
        "joy_cat"         	        to "4557741be111e94ca7083abf39422a30",
        "smile_cat" 	            to "e96ed3e46f59af0b9b7b49e3cb4e59f7",
        "soul"                      to "837473951885426729",
        "troll"	                    to "970017316756656218",
        "grin"	                    to "ab226fe832acc948e9f974decd04a4f0",
        "meth"	                    to "1009532398175797268",
        "salud"        	            to "1010631450737000528",
        "question"     	            to "3e531d8e171629e9433db0bb431b2e12",
        "huevo"	                    to "938012890173153283",
        "eggy"         	            to "938012890173153283",
        "clueless"     	            to "949461776087535626",
        "angyguy"	                to "1009326191121014814",
        "segs"	                    to "792849609126838282",
        "surprise"                  to "900110235761532968",
        "peeposcout"                to "1010981809602961418",
        "floppaeye"	                to "954738126889291837",
        "peepoberserk" 	            to "1000104482056704000",
        "peeporiot"	                to "925412123956359228",
        "sexgod"	                to "1010017697305735228",
        "tsbee"	                    to "1012566897709035520",
        "laruking"     	            to "808274760219361301",
        "chester"	                to "909518480213176320",
        "abdulfloppa"	            to "967180247143415858",
        "basedfloppa"	            to "967180247130857582",
        "floppawave"	            to "967180853627224134",
        "peepofloppalove"	        to "967169894502240376",
        "floppasurprised"	        to "784383419823947786",
        "floppashy"	                to "806937807771795486",
        "floppatux"   	            to "755890317139378260",
        "cry"	                    to "f6d30507f4baee759bc9d7e5c0d3ba4f",
        "sob"          	            to "f7b3f6b926cb31a17d4928d076febab4",
        "pleading_face"	            to "6bca769662f755d33514d1f5304c617d",
        "face_with_raised_eyebrow"	to "879468637365983eba26983ddf6d38ad",
        "thumbsup"                  to "08c0a077780263f3df97613e58e71744",
        "thumbsdown"	            to "66e3cbf517993ee5261f23687a2bc032",
        "smirk"	                    to "480eb6b0f6c22cbc1d98d6ff93e1edc1",
        "floppadrip"	            to "1007308413602115605",

    )
    private val regexString = emoteMap.keys.joinToString(":|:","(:",":)")
    private val regex = Regex(regexString, RegexOption.IGNORE_CASE)
}