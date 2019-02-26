import { AttributtType, Attributt } from './Types'
import AttributtListe from './Attributter'

//TODO: Move common functions in AttributtManager to this file.

//TODO: Implement resultset for attributes for selection
// export const getAttributesForSelection = (): Attributt => {}

const _attributesFlatMap = attributes => {
	const attributeFlatMap = attribute =>
		attribute.items
			? [attribute].concat(...attribute.items.map(i => attributeFlatMap(i)))
			: [attribute]
	return [].concat(...attributes.map(attribute => attributeFlatMap(attribute)))
}

const traverseDependencies = (attributter: Attributt[]): { [key: string]: Attributt[] } => {
	return attributter
		.filter(attr => attr.includeIf)
		.map(attr => ({
			attribute: attr,
			dependencies: attr.includeIf
				.map(e => e.id)
				.concat(...Object.keys(traverseDependencies(attr.includeIf)))
		}))
		.map(value =>
			[value].concat(
				attributter
					.filter(attribute => attribute.parent === value.attribute.id)
					.map(attribute => ({ attribute: attribute, dependencies: value.dependencies }))
			)
		)
		.reduce((res, attributes) => {
			attributes.forEach(dep => {
				dep.dependencies.forEach(attr => {
					res[attr] = res[attr] ? res[attr].concat([dep.attribute]) : [dep.attribute]
				})
			})
			return res
		}, {})
}

export const isAttributtEditable = (attr: Attributt): boolean => {
	return (
		attr.attributtType === AttributtType.SelectAndEdit ||
		attr.attributtType === AttributtType.SelectAndRead ||
		attr.attributtType === AttributtType.EditOnly
	)
}

export const DependencyTree = traverseDependencies(_attributesFlatMap(AttributtListe))