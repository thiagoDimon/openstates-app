export interface PoliticianRole {
  id: string
  title: string
  orgClassification: string
  district: string
  jurisdictionName: string
  jurisdictionId: string
}

export interface Politician {
  id: string
  name: string
  givenName: string
  familyName: string
  party: string
  imageUrl: string | null
  email: string | null
  gender: string | null
  birthDate: string | null
  openstatesUrl: string | null
  roles: PoliticianRole[]
}

export interface FilterOptions {
  states: string[]
  parties: string[]
}
