const path = require('path')
const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const Dotenv = require('dotenv-webpack')
const pkg = require('./package.json')
const GitRevisionPlugin = require('git-revision-webpack-plugin')

const gitRevisionPlugin = new GitRevisionPlugin({
	commithashCommand: 'rev-parse --short HEAD'
})

// Buildtype
const TARGET = process.env.npm_lifecycle_event

// Set env variable
process.env.NODE_ENV = process.env.NODE_ENV || 'development'
const devMode = process.env.NODE_ENV !== 'production'

const outputDir = {
	development: 'dist/dev',
	production: 'dist/production'
}

const webpackConfig = {
	mode: process.env.NODE_ENV,
	devtool: 'source-map',
	entry: ['babel-polyfill', './src/index.js'],
	output: {
		filename: 'bundle.js',
		publicPath: '/',
		path: path.join(__dirname, 'dist')
	},
	stats: 'minimal',
	devServer: {
		stats: 'minimal',
		contentBase: path.join(__dirname, 'public'),
		historyApiFallback: true,
		proxy: {
			'/external/dolly/api/v1': {
				target: 'http://localhost:8080',
				pathRewrite: { '^/external/dolly': '' }
			},
			'/external/tpsf': {
				target: 'https://tps-forvalteren-u2.nais.preprod.local',
				pathRewrite: { '^/external/tpsf': '' },
				secure: false,
				changeOrigin: true
			}
		}
	},
	plugins: [
		new webpack.DefinePlugin({
			'process.env': {
				NODE_ENV: JSON.stringify(process.env.NODE_ENV) || '"development"'
			},
			BUILD: {
				VERSION: JSON.stringify(pkg.version) || '""',
				COMMITHASH: JSON.stringify(gitRevisionPlugin.commithash()),
				BRANCH: JSON.stringify(gitRevisionPlugin.branch())
			}
		}),
		new MiniCssExtractPlugin({
			// Options similar to the same options in webpackOptions.output
			// both options are optional
			filename: devMode ? '[name].css' : '[name].[contenthash:8].css'
		}),
		new HtmlWebpackPlugin({
			title: 'Dolly',
			favicon: 'src/assets/favicon.ico',
			inject: false,
			template: require('html-webpack-template'),
			appMountId: 'root'
		})
	],
	resolve: {
		alias: {
			'~': path.resolve(__dirname, 'src'),
			lessVars: path.resolve(__dirname, 'src/styles/variables.less'),
			lessUtils: path.resolve(__dirname, 'src/styles/utils.less')
		},
		extensions: ['.js', '.json', '.ts', '.tsx']
	},
	module: {
		rules: [
			{
				test: /\.ts(x?)$/,
				exclude: /node_modules/,
				use: ['babel-loader', 'ts-loader']
			},
			{
				test: /\.js$/,
				exclude: /node_modules/,
				use: ['babel-loader']
			},
			{
				test: /\.less$/,
				use: [
					devMode ? 'style-loader' : MiniCssExtractPlugin.loader,
					'css-loader',
					'less-loader?{"globalVars":{"nodeModulesPath":"\'~\'", "coreModulePath":"\'~\'"}}'
				]
			},
			{
				test: /\.css$/,
				use: ['style-loader', 'css-loader']
			},
			{
				// images
				test: /\.(ico|jpe?g|png|gif|woff|woff2|eot|otf|ttf|svg)$/,
				use: ['file-loader']
			}
		]
	}
}

// If dev build
if (TARGET === 'build-dev') {
	webpackConfig.output = {
		path: path.join(__dirname, outputDir.development),
		filename: 'bundle.js',
		publicPath: '/'
	}
}

// If production build
if (TARGET === 'build') {
	webpackConfig.devtool = 'none'
	webpackConfig.output = {
		path: path.join(__dirname, outputDir.production),
		filename: 'bundle.[contenthash:8].js',
		publicPath: '/'
	}
	webpackConfig.plugins = [
		new CleanWebpackPlugin([outputDir.production]),
		new Dotenv({
			path: path.resolve(__dirname, '.env.prod'),
			systemvars: true
		})
	].concat(webpackConfig.plugins)
}

module.exports = webpackConfig
