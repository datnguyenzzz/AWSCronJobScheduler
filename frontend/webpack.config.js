const path = require('path')
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const TerserPlugin = require('terser-webpack-plugin')
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

const isProduction = process.env.NODE_ENV === 'production'

const config = {
    //Source maps solve this problem by providing a mapping between the original and the transformed source code
    //devtool: !isProduction ? 'source-map' : false, //generate source map for prod
    entry: './src/index.tsx',
    output : { // path for output the bundles
        path: path.resolve(__dirname, 'dist'),
        filename: '[name].[hash:8].js',
        sourceMapFilename: '[name].[hash:8].map',
        chunkFilename: '[id].[hash:8].js',
        publicPath: '/'
    },
    resolve: {
        extensions: [
            '.ts', '.tsx',
            '.js', '.jsx',
            '.css'
        ] //Extensions we wanna support
    },
    target: 'web',
    mode: isProduction ? 'production' : 'development', //production mode minifies code
    module: {
        //how to load external dependencies
        //set a specific loader for each file
        rules: [
            {
                test: /\.(ts|tsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: 'ts-loader',
                    options: {
                        transpileOnly: true
                    }
                }
            },
            // css module
            {
                test: /\.module\.css$/,
                use: [
                    //'style-loader',
                    MiniCssExtractPlugin.loader,
                    {
                        loader: 'css-loader',
                        options: {
                            modules: { //css module class name
                                localIdentName: "[name]_[local]__[hash:base64:5]"
                            },
                        }
                    }
                ],
            },
            // css in node_modules
            {
                test: /\.css$/,
                exclude: /\.module\.css$/,
                use: [
                    //'style-loader',
                    MiniCssExtractPlugin.loader,
                    'css-loader'
                ]
            }
        ]
    },
    plugins:  [
        //mini css extract plugin is tool for gathering all CS into chunks
        new MiniCssExtractPlugin({
            filename: '[name].[hash:8].css',
            ignoreOrder: true
        }),
        new HtmlWebpackPlugin({
            title: 'AWS Cronjob project',
            template: './public/index.html',
            filename: './main.html'
        }),
    ],
    optimization: {
        //smaller form
        //They complement the minification technique and can be
        //split into scope hoisting, pre-evaluation, and improving
        //parsing.
        /**
         * Since webpack 4, it applies scope hoisting in production mode
         * by default
         *
         *
         */
        minimizer: [
            //JS minifier
            new TerserPlugin(),
            //Css minifier
            new CssMinimizerPlugin({
                minimizerOptions: {
                    preset: ["default"]
                }
            })
        ],
        splitChunks: {
            //chunks is module imported inside the module (cachable module)
            //chunk will be "merged" if it had been called by multiple module
            /**
             *  ├── assets
                │   ├── css
                │   │   ├── editor.blocks.css
                │   │   ├── style.blocks.css
                │   ├── js
                │   │   ├── editor.blocks.js
                ├── blocks
                │   ├── block1
                │   │   ├── editor.scss <--- shared chunk
                │   │   ├── style.scss <--- shared chunk
                │   │   ├── index.js <--- child of entry
                │   ├── block2
                │   │   ├── editor.scss <--- shared chunk
                │   │   ├── style.scss <--- shared chunk
                │   │   ├── index.js <--- child of entry
                │   ├── block3
                │   │   ├── editor.scss <--- shared chunk
                │   │   ├── style.scss <--- shared chunk
                │   │   ├── index.js <--- child of entry
                │   ├── index.js <--- entry

             */
            cacheGroups: {
                default: false,
                //splitting to vendor.js for node_modules
                defaultVendors: {
                    test: /node_modules/,
                    name: 'vendor',
                    chunks: 'all'
                }
            }
        }
    }
}

//export
module.exports = config
