# Check if resourcepack.zip exists
if (Test-Path "resourcepack.zip") {
  Write-Output "Resourcepack already compacted"

  # Prompt the user with a choice
  $choice = Read-Host "Delete and recompress? (Y/N) "
  
  if ($choice -eq 'N' -or $choice -eq 'n') {
    Get-FileHash -Path "resourcepack.zip" -Algorithm SHA1 | Format-List
    Write-Output "Exiting..."
    exit
  } else {
    Read-Host "Press ENTER to continue with the deletion of resourcepack.zip"
    Remove-Item -Force "resourcepack.zip"
  }
}

# Change directory to 'uncompressed'
Set-Location -Path "uncompressed"
7z a -tzip resourcepack.zip assets pack.mcmeta pack.png
Copy-Item resourcepack.zip ..
Remove-Item -Force resourcepack.zip
Set-Location -Path ".."
Get-FileHash -Path "resourcepack.zip" -Algorithm SHA1 | Format-List