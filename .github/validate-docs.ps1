# Documentation Validation Script
# Run this to check if AI assistants violated the 11-file policy

$mdFiles = Get-ChildItem -Path $PSScriptRoot\.. -Recurse -Filter "*.md"
$count = $mdFiles.Count
$target = 11

Write-Host "`n=== DOCUMENTATION FILE COUNT CHECK ===" -ForegroundColor Cyan

if ($count -eq $target) {
    Write-Host "✅ PASS: Found $count markdown files (target: $target)" -ForegroundColor Green
    exit 0
}
else {
    Write-Host "❌ FAIL: Found $count markdown files (target: $target)" -ForegroundColor Red
    Write-Host "`nUnexpected files:" -ForegroundColor Yellow
    
    $expected = @(
        "README.md",
        "analysis\README.md",
        "docs\ANALYSIS.md",
        "docs\ARCHITECTURE.md",
        "docs\BALANCE_ADJUSTMENTS.md",
        "docs\CROSS_PLATFORM.md",
        "docs\DATA_ANALYSIS.md",
        "docs\GETTING_STARTED.md",
        "docs\INDEX.md",
        "docs\SEED_SYSTEM.md",
        "docs\UI_GUIDE.md"
    )
    
    foreach ($file in $mdFiles) {
        $relativePath = $file.FullName.Replace($PSScriptRoot + "\..\", "")
        if ($relativePath -notin $expected) {
            Write-Host "  ⚠️  $relativePath" -ForegroundColor Red
        }
    }
    
    Write-Host "`nTo fix: Delete unexpected files and update docs/INDEX.md" -ForegroundColor Yellow
    exit 1
}
