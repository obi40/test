var gulp = require('gulp'),
    sass = require('gulp-sass'),
    stripCssComments = require('gulp-strip-css-comments'),
    rename = require('gulp-rename'),
    cssnano = require('gulp-cssnano'),
    minifyCss = require('gulp-clean-css'),
    os = require('os'),
    notify = require("gulp-notify"),
    browserSync = require('browser-sync').create(),
    reload = browserSync.reload,
    jshint = require('gulp-jshint'),
    stylish = require('jshint-stylish'),
    fileSys = require('fs'),
    colors = require('colors'),
    mkfiles = require('mkfiles'),
    beautify = require('js-beautify').js_beautify,
    uglify = require('gulp-uglify'),
    pump = require('pump'),
    htmlmin = require('gulp-htmlmin');

var mainDirectory = "src/main/webapp/"
var componentDirectory = mainDirectory + 'js/modules/component';

var paths = {
    sassFiles: 'sass/**/*.scss',
    cssFiles: mainDirectory + 'assets/css',
    jsFiles: mainDirectory + 'js/**/*.js',
    htmlFiles: mainDirectory + 'js/modules/**/*.html'
}

var versionNo = "";

//pass the version number as 'gulp build --v1.0.0'
gulp.task('build', function () {
    if (process.argv.length <= 3) {
        console.log('\nEnter version number!\n^^^^^^^^^^^^^^^^^^^^^'.red);
        return;
    }
    versionNo = process.argv[3].substring(2);
    gulp.start('minifyJs');
    gulp.start('minifyHtml');
    gulp.start('minifyCss');
});

gulp.task('minifyJs', function (cb) {
    pump([
        gulp.src(paths.jsFiles),
        uglify({
            mangle: {
                reserved: []
            }
        }),
        gulp.dest(mainDirectory + 'dist-' + versionNo)
    ],
        cb
    );
});

gulp.task('minifyHtml', function () {
    return gulp.src(paths.htmlFiles)
        .pipe(htmlmin({
            collapseWhitespace: true,
            removeComments: true,
            keepClosingSlash: true
        }))
        .pipe(gulp.dest(mainDirectory + 'dist-' + versionNo + '/modules'));
});

gulp.task('minifyCss', function (done) {
    gulp.src(paths.sassFiles)
        .pipe(sass({
            errLogToConsole: true,
            sourceComments: true
        })).on('error', sass.logError)
        .pipe(gulp.dest(mainDirectory + 'dist-' + versionNo + '/styles'))
        .pipe(minifyCss({
            compatibility: 'ie8',
            keepSpecialComments: 0
        }))
        .pipe(rename({
            extname: '.min.css'
        }))
        .pipe(gulp.dest(mainDirectory + 'dist-' + versionNo + '/styles'))
        .on('end', done);
});

gulp.task('sass', function (done) {
    gulp.src(paths.sassFiles)
        .pipe(sass({
            errLogToConsole: true,
            sourceComments: true
        })).on('error', sass.logError)
        .pipe(gulp.dest(paths.cssFiles))
        .pipe(minifyCss({
            compatibility: 'ie8',
            keepSpecialComments: 0
        }))
        .pipe(rename({
            extname: '.min.css'
        }))
        .pipe(gulp.dest(paths.cssFiles))
        .on('end', done);
});

function search(string, wordToSearch) {
    var text = string,
        filter = wordToSearch,
        hits = 0,
        array = string.split('\''),
        length = array.length,
        i = 0;
    while (i < length) {
        if (filter === array[i]) {
            hits += 1;
        }
        i += 1;
    }

    if (hits === 0) {
        return true;
    } else {
        return false;
    }
}

var isExistFlag = false;

function readWriteAsync(file, oldString, newString, moduleName, concatString) {
    fileSys.readFile(file, 'utf-8', function (err, data) {
        if (err) throw err;

        if (!concatString) {

            if (search(data, moduleName)) {
                console.log('new route added'.green);
                isExistFlag = false;
            } else {
                console.log('Error Adding Your Route, you might add it before change the module name and try again'.red);
                isExistFlag = true;
                return;
            }
        }
        if (isExistFlag && concatString) {
            return;
        }
        var newValue = concatString ? data + newString : data.replace(oldString, newString);

        fileSys.writeFile(file, newValue, 'utf-8', function (err) {
            if (err) throw err;

            console.log('fileListAsync complete');

        });
    });
}

var replaceCamelHyphen = function (name) {
    name = name.replace(/([A-Z])/g, "-$1").toLowerCase();
    return name;
}
gulp.task('module', function () {

    if (process.argv.length <= 3) {
        console.log('\nYou have to set the module name!\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^'.red);
        return;
    } else {
        var moduleName = process.argv[3].substring(2);
        var viewName = replaceCamelHyphen(moduleName);
        mkfiles({
            path: componentDirectory,
            dirs: [moduleName],
            files: [{
                file: moduleName + 'Controller.js',
                content: {
                    template: "define(['app','config','util'], function(app,config,util) {\n" +
                        "'use strict';\n" +
                        "app.controller('{{name}}Ctrl', [\n" +
                        "'$scope',\n" +
                        " '{{name}}Service',\n" +
                        "function(\n" +
                        " $scope,\n" +
                        " {{name}}Service\n" +
                        " ) {\n" +
                        " //this is {{name}} controller, your code goes here\n" +
                        " }\n" +
                        "  ]);\n" +
                        " });\n", // specify the content by template string（only support handlebars）
                    data: { name: moduleName }
                }
            }, {
                file: moduleName + 'Service.js',
                content: {
                    template: "define(['app', 'util'], function(app, util) {\n" +
                        " 'use strict';\n" +

                        " app.service('{{name}}Service', function() {\n" +
                        " //this is {{name}} service, your code goes here\n" +

                        " });\n" +

                        "});\n", // specify the content by template string（only support handlebars）
                    data: { name: moduleName }
                }
            }, {
                file: viewName + '-view.html',
                content: {
                    template: "this is the {{name}} view ", // specify the content by template file（only support handlebars）, file content is 'My name is {{name}}'
                    data: { name: viewName }
                }
            }]
        });

        mkfiles({
            path: "./sass",
            // dirs: ["sass"],
            files: [{
                file: '_' + moduleName + '.scss',
                content: {
                    template: "//this is the partial scss file for {{name}} ", // specify the content by template file（only support handlebars）, file content is 'My name is {{name}}'
                    data: { name: moduleName }
                }
            }

            ]
        });

        var oldRoute = "//Don't add anything under this line, add your routes before it and Don't REMOVE",
            newRoute =
                ",\n'" + viewName + "': {\n" +
                "url: prefix+'" + viewName + "',\n" +
                "dependencies: ['modules/component/" + moduleName + "/" + moduleName + "Controller', 'modules/component/" + moduleName + "/" + moduleName + "Service'],\n" +
                "directives: [],\n" +
                "views: {\n" +
                "main: {\n" +
                "templateUrl: 'js/modules/component/" + moduleName + "/" + viewName + "-view.html',\n" +
                "controller: '" + moduleName + "Ctrl',\n" +
                "data: {pageName:\"" + moduleName + "\"\n" +
                "}\n" +
                "}\n" +
                " }\n" +
                "}" +
                "//Don't add anything under this line, add your routes before it and Don't REMOVE\n";
        readWriteAsync(mainDirectory + 'js/routes.js', oldRoute, newRoute, moduleName, false);
        var partial = "@import \"" + moduleName + "\";" + "\n\r";
        readWriteAsync('sass/_general.scss', '', partial, moduleName, true);
    }

});

gulp.task('deleteModule', function () {
    if (process.argv.length <= 3) {
        console.log('\nyou have to set the module name\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^'.red);
        return;
    } else {
        var moduleName = process.argv[3].substring(2);
        var viewName = replaceCamelHyphen(moduleName);

    }
})

gulp.task('sass:watch', function () {
    gulp.watch(paths.sassFiles, ['sass']).on("change", reload);
});

gulp.task('js:watch', function () {
    gulp.watch(mainDirectory + '**/*.js', ['lint']).on("change", reload);
});

gulp.task('lint', function () {
    return gulp.src(mainDirectory + 'js/modules/component/**/*.*')
        .pipe(jshint()) //'.jshintrc'
        .pipe(jshint.reporter(stylish));
});


gulp.task('serve', function () {
    browserSync.init(mainDirectory + "**/*.html", {
        injectChanges: true,
        server: {
            baseDir: mainDirectory,
        },
        port: 4000
    });
    gulp.watch(mainDirectory + "**/*.html").on("change", reload);
});

gulp.task('default', ['sass', 'sass:watch', 'serve']);