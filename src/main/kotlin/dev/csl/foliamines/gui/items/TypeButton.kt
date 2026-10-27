package dev.csl.foliamines.gui.items

import xyz.xenondevs.invui.item.impl.CycleItem

class CycleButton() : CycleItem(InstantTypeButton().itemProvider, DelayTypeButton().itemProvider, PercentageTypeButton().itemProvider)