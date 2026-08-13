# Compose, DataStore, ViewModel, and Coroutines all ship their own consumer
# proguard rules (keeping constructors/members actually reached via reflection).
# Broad "-keep ... { *; }" rules here defeat R8 shrinking for these libraries
# (material-icons-extended alone is tens of MB unshrunk) and were the main
# cause of oversized dex/installed size. Only suppress warnings, don't keep.
-dontwarn androidx.compose.**
-dontwarn kotlinx.coroutines.**
