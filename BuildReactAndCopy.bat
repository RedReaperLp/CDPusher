@echo off
cd soundfetcher
echo Building project with Vite in soundfetcher folder...
call npx vite build
cd ..
echo Build process completed.
echo Copying build files to public folder...
xcopy /E /Y soundfetcher\dist\assets\index.js assets\js\SoundFetcher.js
xcopy /E /Y soundfetcher\dist\assets\index.css assets\css\SoundFetcher.css
