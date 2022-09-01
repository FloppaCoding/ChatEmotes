package chatemotes.impl

import net.minecraft.util.ResourceLocation

object EmoteHandler {
    // A lot of the actual implementation is in the MixinFontRenderer to make accessing private methods and fields easier.

    /**
     * Returns a list of all matches for the emote
     */
    fun emoteMatches(text: String): List<String>{
        val matches = regex.findAll(text)
        return matches.map { it.groupValues[1].removeSurrounding(":") }.toList()
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
    )
    private val regexString = emoteMap.keys.joinToString(":|:","(:",":)")
    private val regex = Regex(regexString, RegexOption.IGNORE_CASE)
}