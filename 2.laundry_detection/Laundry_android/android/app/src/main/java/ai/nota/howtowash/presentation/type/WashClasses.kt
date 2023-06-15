package ai.nota.howtowash.presentation.type

import ai.nota.howtowash.R
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class WashType(val type: String) {
    Wash("세탁 시"),
    Ironing("다림질 시"),
    Drying("건조 방식"),
    DryCleaning("드라이 클리닝 시"),
    WetCleaning("웻 클리닝 시"),
    Wring("말리기 전에"),
    Bleach("표백 시")
}

enum class WashClasses(private val washType: WashType, @StringRes private val explainRes: Int, @DrawableRes private vararg val iconRes: Int) {
    MachineWash(WashType.Wash, R.string.how_to_machine_wash, R.drawable.ic_machine_wash),
    MachineWash30(WashType.Wash, R.string.how_to_machine_wash_30, R.drawable.ic_machine_wash_30),
    MachineWash40(WashType.Wash, R.string.how_to_machine_wash_30, R.drawable.ic_machine_wash_30),//MachineWash40(WashType.Wash, R.string.how_to_machine_wash_40, R.drawable.ic_machine_wash_40),
    MachineWash50(WashType.Wash, R.string.how_to_machine_wash_50, R.drawable.ic_machine_wash_50),
    MachineWash60(WashType.Wash, R.string.how_to_machine_wash_60, R.drawable.ic_machine_wash_60),
    MachineWash70(WashType.Wash, R.string.how_to_machine_wash_70, R.drawable.ic_machine_wash_70),
    MachineWash95(WashType.Wash, R.string.how_to_machine_wash_95, R.drawable.ic_machine_wash_95),
    IroningAtLowTemperatures(WashType.Ironing, R.string.ironing_at_low_temperatures, R.drawable.ic_ironing_at_low_temperature),
    DelicateDryCleaningWithPCE(WashType.DryCleaning, R.string.delicate_dry_cleaning_PCE, R.drawable.ic_delicate_dry_cleaning_pce),
    DoNotBleachWithChlorine(WashType.Bleach, R.string.do_not_bleach_with_chlorine, R.drawable.ic_do_not_bleach_with_chlorine),
    DryCleaningAllowed(WashType.DryCleaning, R.string.dry_cleaning_allowed, R.drawable.ic_dry_cleaning_allowed),
    DryCleaningProhibited(WashType.DryCleaning, R.string.dry_cleaning_prohibited, R.drawable.ic_dry_cleaning_prohibited),
    DryCleaningWithPCE(WashType.DryCleaning, R.string.dry_cleaning_PCE, R.drawable.ic_dry_cleaning_pce),
    TumbleDryingAtHighTemperatures(WashType.Drying, R.string.tumble_drying_at_high_temperature, R.drawable.ic_tumble_drying_high_temperature),
    TumbleDryingAtLowTemperatures(WashType.Drying, R.string.tumble_drying_at_low_temperature, R.drawable.ic_tumble_drying_low_temperature),
    DryingFlat(WashType.Drying, R.string.drying_flat, R.drawable.ic_drying_flat),
    DryingDrip(WashType.Drying, R.string.drying_drip, R.drawable.ic_drying_drip),
    HandWashOnly(WashType.Wash, R.string.hand_wash_only, R.drawable.ic_hand_wash_only),
    IroningAtHighTemperatures(WashType.Ironing, R.string.ironing_at_high_temperatures, R.drawable.ic_ironing_at_high_temperature),
    IroningAtMediumTemperatures(WashType.Ironing, R.string.ironing_at_medium_temperatures, R.drawable.ic_ironing_at_medium_temperature),
    IroningIsProhibited(WashType.Ironing, R.string.ironing_is_prohibited, R.drawable.ic_ironing_is_prohibited),
    TumbleDryingNormal(WashType.Drying, R.string.tumble_drying_normal, R.drawable.ic_tumble_drying_normal),
    TumbleDryingIsProhibited(WashType.Drying, R.string.tumble_drying_is_prohibited, R.drawable.ic_tumble_drying_is_prohibited),
    WashingProhibited(WashType.Wash, R.string.washing_prohibited, R.drawable.ic_washing_prohibited),
    BleachAllowed(WashType.Bleach, R.string.bleach_allowed, R.drawable.ic_bleach_allowed),
    BleachProhibited(WashType.Bleach, R.string.bleach_prohibited, R.drawable.ic_bleach_prohibited, R.drawable.ic_bleach_prohibited_2),
    TumbleDryingAtMediumTemperatures(WashType.Drying, R.string.tumble_drying_at_medium_temperature, R.drawable.ic_tumble_drying_medium_temperature),
    DryingFlatShade(WashType.Drying, R.string.drying_flat_shade, R.drawable.ic_drying_flat_shade),
    SteamingProhibited(WashType.Ironing, R.string.steaming_prohibited, R.drawable.ic_steaming_is_prohibited),
    WetCleaningProhibited(WashType.WetCleaning, R.string.wet_cleaning_prohibited, R.drawable.ic_wet_cleaning_prohibited),
    WringProhibited(WashType.Wring, R.string.wring_prohibited, R.drawable.ic_wring_prohibited),
    BleachWithChlorine(WashType.Bleach, R.string.bleach_with_chlorine, R.drawable.ic_bleach_with_chlorine),
    DryCleaningAnySolvent(WashType.DryCleaning, R.string.dry_cleaning_any_solvent, R.drawable.ic_dry_cleaning_any_solvent),
    DryCleaningAtLowTemperatures(WashType.DryCleaning, R.string.dry_cleaning_at_low_temperatures, R.drawable.ic_dry_cleaning_at_low_temperature),
    DryCleaningSteamProhibited(WashType.DryCleaning, R.string.dry_cleaning_steam_prohibited, R.drawable.ic_dry_cleaning_steam_prohibited),
    DryCleaningPetroleumOnly(WashType.DryCleaning, R.string.dry_cleaning_petroleum_only, R.drawable.ic_dry_cleaning_petroleum_only),
    DryCleaningReducedMoisture(WashType.DryCleaning, R.string.dry_cleaning_reduced_moisture, R.drawable.ic_dry_cleaning_reduced_moisture),
    DryCleaningShortCycle(WashType.DryCleaning, R.string.dry_cleaning_short_cycle, R.drawable.ic_dry_cleaning_short_cycle),
    IroningIsAllowed(WashType.Ironing, R.string.ironing_is_allowed, R.drawable.ic_ironing_is_allowed),
    DryingLineShade(WashType.Drying, R.string.drying_line_shade, R.drawable.ic_drying_shade),
    DryingAllowed(WashType.Drying, R.string.drying_allowed, R.drawable.ic_drying_allowed),
    DryingShade(WashType.Drying, R.string.drying_shade, R.drawable.ic_drying_shade),
    SteamingAllowed(WashType.Ironing, R.string.steaming_allowed, R.drawable.ic_steaming_allowed),
    TumbleDryingNoTemperature(WashType.Drying, R.string.tumble_drying_no_temperature, R.drawable.ic_tumble_drying_no_temperature),
    WetCleaningAllowed(WashType.WetCleaning, R.string.wet_cleaning_allowed, R.drawable.ic_wet_cleaning_allowed),
    WringAllowed(WashType.Wring, R.string.wring_allowed, R.drawable.ic_wring_allowed);

    operator fun component1() = washType
    operator fun component2() = explainRes
    operator fun component3() = iconRes
}