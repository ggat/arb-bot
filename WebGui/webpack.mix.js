let mix = require('laravel-mix');

/*
 |--------------------------------------------------------------------------
 | Mix Asset Management
 |--------------------------------------------------------------------------
 |
 | Mix provides a clean, fluent API for defining some Webpack build steps
 | for your Laravel application. By default, we are compiling the Sass
 | file for the application as well as bundling up all the JS files.
 |
 */

/*mix.js('resources/assets/js/app.js', 'public/js')
   .sass('resources/assets/sass/app.scss', 'public/css');*/
/*<link rel="stylesheet" href="../assets/css/bootstrap-yeti.min.css">
    <link rel="stylesheet" href="../assets/css/chosen.css">
    <link rel="stylesheet" href="../assets/css/chosen-bootstrap.css">
    <link rel="stylesheet" href="../assets/css/chosen-bootstrap-yeti.css">
    <link rel="stylesheet" href="../assets/css/app.css">*/

mix.webpackConfig({ devtool: "inline-source-map" });

mix.copyDirectory('resources/assets/img', 'public/img');
mix.copyDirectory('resources/assets/css', 'public/css');
mix.copyDirectory('resources/assets/js/lib', 'public/js/lib');
mix.js('resources/assets/js/app.js', 'public/js')
    .sourceMaps();
//mix.copyDirectory('resources/assets/js', 'public/js');