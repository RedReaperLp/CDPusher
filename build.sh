cd /root/Reaper/projects/CDPusher/soundfetcher
npx vite build
cd ..

yes | cp -rf soundfetcher/dist/assets/index.js assets/js/index.js
yes | cp -rf soundfetcher/dist/assets/index.css assets/css/index.css
yes | cp -rf soundfetcher/dist/assets/index2.js assets/js/index2.js