import { useState } from 'react'
import { Box, Button, CircularProgress, Container, Typography } from '@mui/material'
import SearchIcon from '@mui/icons-material/Search'
import { usePoliticians } from '@/hooks/usePoliticians'
import { FilterBar } from '@/components/FilterBar'
import { PoliticianGrid } from '@/components/PoliticianGrid'

export function PoliticiansPage() {
  const [state, setState] = useState('')
  const [party, setParty] = useState('')
  const [appliedState, setAppliedState] = useState<string | undefined>(undefined)
  const [appliedParty, setAppliedParty] = useState<string | undefined>(undefined)

  const {
    data,
    isLoading,
    isError,
    error,
    isFetched,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = usePoliticians(appliedState, appliedParty)

  const politicians = data?.pages.flatMap(p => p.content) ?? []
  const errorMessage = isError && error instanceof Error ? error.message : undefined

  function handleSearch() {
    setAppliedState(state || undefined)
    setAppliedParty(party || undefined)
  }

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
        US Politicians
      </Typography>

      <Box sx={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: 2, mb: 3 }}>
        <FilterBar
          state={state}
          party={party}
          onStateChange={setState}
          onPartyChange={setParty}
        />

        <Button
          variant="contained"
          onClick={handleSearch}
          disabled={isLoading || !state}
          startIcon={<SearchIcon />}
          sx={{ ml: 'auto' }}
        >
          Search
        </Button>
      </Box>

      {isLoading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
          <CircularProgress />
        </Box>
      )}

      {!isLoading && !isFetched && (
        <Typography color="text.secondary" sx={{ mt: 4, textAlign: 'center' }}>
          Select a state to list the politicians and click Search.
        </Typography>
      )}

      {!isLoading && isFetched && (
        <PoliticianGrid
          politicians={politicians}
          isError={isError}
          errorMessage={errorMessage}
          onLoadMore={fetchNextPage}
          hasMore={hasNextPage ?? false}
          isLoadingMore={isFetchingNextPage}
        />
      )}
    </Container>
  )
}
